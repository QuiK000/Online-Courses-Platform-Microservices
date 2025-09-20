package com.dev.quikkkk.auth_service.service.impl;

import com.dev.quikkkk.auth_service.client.IUserServiceClient;
import com.dev.quikkkk.auth_service.dto.request.CreateUserRequest;
import com.dev.quikkkk.auth_service.dto.request.LoginRequest;
import com.dev.quikkkk.auth_service.dto.request.RefreshTokenRequest;
import com.dev.quikkkk.auth_service.dto.request.RegistrationRequest;
import com.dev.quikkkk.auth_service.dto.request.UpdateRoleRequest;
import com.dev.quikkkk.auth_service.dto.response.AuthenticationResponse;
import com.dev.quikkkk.auth_service.dto.response.UserResponse;
import com.dev.quikkkk.auth_service.entity.Role;
import com.dev.quikkkk.auth_service.entity.UserCredentials;
import com.dev.quikkkk.auth_service.exception.BusinessException;
import com.dev.quikkkk.auth_service.mapper.UserMapper;
import com.dev.quikkkk.auth_service.repository.IRoleRepository;
import com.dev.quikkkk.auth_service.repository.IUserCredentialsRepository;
import com.dev.quikkkk.auth_service.service.IAuthenticationService;
import com.dev.quikkkk.auth_service.service.IBruteForceProtectionService;
import com.dev.quikkkk.auth_service.service.IEmailVerificationService;
import com.dev.quikkkk.auth_service.service.IJwtService;
import com.dev.quikkkk.auth_service.service.ITokenBlackListService;
import com.dev.quikkkk.auth_service.utils.NetworkUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.dev.quikkkk.auth_service.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.dev.quikkkk.auth_service.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.dev.quikkkk.auth_service.exception.ErrorCode.INVALID_CREDENTIALS;
import static com.dev.quikkkk.auth_service.exception.ErrorCode.PASSWORD_MISMATCH;
import static com.dev.quikkkk.auth_service.exception.ErrorCode.ROLE_NOT_FOUND;
import static com.dev.quikkkk.auth_service.exception.ErrorCode.TOO_MANY_ATTEMPTS;
import static com.dev.quikkkk.auth_service.exception.ErrorCode.USERNAME_ALREADY_EXISTS;
import static com.dev.quikkkk.auth_service.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements IAuthenticationService {
    private final static String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;
    private final IUserCredentialsRepository userRepository;
    private final IBruteForceProtectionService bruteForceProtectionService;
    private final ITokenBlackListService tokenBlackListService;
    private final IUserServiceClient userServiceClient;
    private final IRoleRepository roleRepository;
    private final UserMapper mapper;
    private final IEmailVerificationService emailVerificationService;

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        log.info("Login request: {}", request);
        String clientIp = NetworkUtils.getClientIp().orElseThrow(() -> new BusinessException(INTERNAL_SERVER_ERROR));

        if (bruteForceProtectionService.isBlocked(clientIp)) throw new BusinessException(TOO_MANY_ATTEMPTS);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            bruteForceProtectionService.registerSuccessfulAttempt(clientIp);

            UserCredentials userCredentials = findUserByUsername(request.getUsername());

            CompletableFuture<String> accessTokenFuture = CompletableFuture.supplyAsync(
                    () -> jwtService.generateAccessToken(userCredentials)
            );

            CompletableFuture<String> refreshTokenFuture = CompletableFuture.supplyAsync(
                    () -> jwtService.generateRefreshToken(userCredentials)
            );

            String accessToken = accessTokenFuture.get();
            String refreshToken = refreshTokenFuture.get();

            log.info("Login response: {}", accessToken);
            return AuthenticationResponse
                    .builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType(TOKEN_TYPE)
                    .build();
        } catch (Exception e) {
            bruteForceProtectionService.registerFailedAttempt(clientIp);
            int remainingAttempts = bruteForceProtectionService.getRemainingAttempts(clientIp);

            log.warn(
                    "Failed login attempt for user: {} from IP: {}. Remaining attempts: {}",
                    request.getUsername(), clientIp, remainingAttempts
            );

            throw new BusinessException(INVALID_CREDENTIALS);
        }
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refresh token request: {}", request);
        String newAccessToken = jwtService.refreshAccessToken(request.getRefreshToken());
        return AuthenticationResponse
                .builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType(TOKEN_TYPE)
                .build();
    }

    @Override
    public void logout(String token) {
        log.info("Logging out user with token: {}", token);

        String actualToken = token.startsWith(TOKEN_TYPE) ? token.substring(TOKEN_TYPE.length()).trim() : token;
        tokenBlackListService.blacklistToken(actualToken);

        log.info("User logged out successfully");
    }

    @Override
    @Transactional
    public void register(RegistrationRequest request) {
        log.info("Registration request: {}", request);
        checkUsername(request.getUsername());
        checkUserEmail(request.getEmail());
        checkPasswords(request.getPassword(), request.getConfirmPassword());

        Role defaultRole = roleRepository
                .findByName("ROLE_STUDENT")
                .orElseThrow(() -> new EntityNotFoundException("Role user does not exist"));

        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);

        UserCredentials userCredentials = mapper.toUser(request);

        userCredentials.setRoles(roles);
        userCredentials.setEnabled(false);
        userCredentials.setEmailVerified(false);

        log.debug("Saving user: {}", userCredentials);
        userRepository.save(userCredentials);
        log.info("User {} registered", userCredentials.getUsername());

        String ipAddress = NetworkUtils.getClientIp().orElseThrow(() -> new BusinessException(INTERNAL_SERVER_ERROR));

        emailVerificationService.sendVerificationCode(
                userCredentials.getId(),
                userCredentials.getEmail(),
                ipAddress
        );

        defaultRole.getUserCredentials().add(userCredentials);
        roleRepository.save(defaultRole);

        try {
            CreateUserRequest userRequest = CreateUserRequest
                    .builder()
                    .id(userCredentials.getId())
                    .username(userCredentials.getUsername())
                    .email(userCredentials.getEmail())
                    .firstName(userCredentials.getFirstName())
                    .lastName(userCredentials.getLastName())
                    .role(userCredentials.getRoles().iterator().next().getName())
                    .build();

            userServiceClient.createUser(userRequest);
            log.info("User profile created in User Service");
        } catch (Exception e) {
            log.warn("Failed to create user profile in User Service: {}", e.getMessage());
        }
    }

    @Override
    public UserResponse getUserById(String id) {
        try {
            UserResponse userFromService = userServiceClient.getUserById(id);
            log.info("User data retrieved from User Service: {}", userFromService);
            return userFromService;
        } catch (Exception e) {
            log.warn("Failed to get user from User Service, falling back to Auth Service: {}", e.getMessage());
            return userRepository
                    .findById(id)
                    .map(mapper::toUserResponse)
                    .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        }
    }

    @Override
    public UserCredentials findUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void updateUserRole(String userId, UpdateRoleRequest request) {
        log.info("Updating user role for user with id: {} to role: {}", userId, request.getRole());

        UserCredentials user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        Role targetRole = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new BusinessException(ROLE_NOT_FOUND));

        Optional<Role> currentRole = user.getRoles().stream().findFirst();

        if (currentRole.isPresent() && currentRole.get().getName().equals(request.getRole())) {
            log.warn("User {} already has role {}", userId, request.getRole());
            return;
        }

        user.getRoles().clear();
        user.getRoles().add(targetRole);

        userRepository.save(user);

        log.info("User role updated from {} to {} for user ID: {}",
                currentRole.map(Role::getName).orElse("NONE"),
                request.getRole(),
                userId
        );

        try {
            userServiceClient.updateUserRole(userId, request);
            log.info("User role updated in User Service");
        } catch (Exception e) {
            log.warn("Failed to update user role in User Service: {}", e.getMessage());
        }
    }

    @Override
    public void deleteUser(String id) {
        log.info("Deleting user with id: {}", id);
        userServiceClient.deleteUser(id);
        userRepository.deleteById(id);
    }

    private void checkUserEmail(String email) {
        boolean emailExists = userRepository.existsByEmailIgnoreCase(email);
        if (emailExists) throw new BusinessException(EMAIL_ALREADY_EXISTS);
    }

    private void checkUsername(String username) {
        boolean usernameExists = userRepository.existsByUsernameIgnoreCase(username);
        if (usernameExists) throw new BusinessException(USERNAME_ALREADY_EXISTS);
    }

    private void checkPasswords(String password, String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) throw new BusinessException(PASSWORD_MISMATCH);
    }
}

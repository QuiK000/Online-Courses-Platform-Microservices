package com.dev.quikkkk.auth_service.service.impl;

import com.dev.quikkkk.auth_service.client.IUserServiceClient;
import com.dev.quikkkk.auth_service.dto.request.CreateUserRequest;
import com.dev.quikkkk.auth_service.dto.request.LoginRequest;
import com.dev.quikkkk.auth_service.dto.request.RefreshTokenRequest;
import com.dev.quikkkk.auth_service.dto.request.RegistrationRequest;
import com.dev.quikkkk.auth_service.dto.response.AuthenticationResponse;
import com.dev.quikkkk.auth_service.dto.response.UserResponse;
import com.dev.quikkkk.auth_service.entity.Role;
import com.dev.quikkkk.auth_service.entity.UserCredentials;
import com.dev.quikkkk.auth_service.exception.BusinessException;
import com.dev.quikkkk.auth_service.exception.ErrorCode;
import com.dev.quikkkk.auth_service.mapper.UserMapper;
import com.dev.quikkkk.auth_service.repository.IRoleRepository;
import com.dev.quikkkk.auth_service.repository.IUserCredentialsRepository;
import com.dev.quikkkk.auth_service.service.IAuthenticationService;
import com.dev.quikkkk.auth_service.service.IJwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.dev.quikkkk.auth_service.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.dev.quikkkk.auth_service.exception.ErrorCode.PASSWORD_MISMATCH;
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
    private final IUserServiceClient userServiceClient;
    private final IRoleRepository roleRepository;
    private final UserMapper mapper;

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        log.info("Login request: {}", request);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserCredentials userCredentials = userRepository
                .findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String token = jwtService.generateAccessToken(userCredentials);
        String refreshToken = jwtService.generateRefreshToken(userCredentials);

        log.info("Login response: {}", token);
        return AuthenticationResponse
                .builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType(TOKEN_TYPE)
                .build();
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

        log.debug("Saving user: {}", userCredentials);
        userRepository.save(userCredentials);
        log.info("User {} registered", userCredentials.getUsername());

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

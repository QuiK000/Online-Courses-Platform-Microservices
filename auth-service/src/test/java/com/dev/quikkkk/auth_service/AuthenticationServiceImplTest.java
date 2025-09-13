package com.dev.quikkkk.auth_service;

import com.dev.quikkkk.auth_service.dto.request.LoginRequest;
import com.dev.quikkkk.auth_service.dto.request.RefreshTokenRequest;
import com.dev.quikkkk.auth_service.dto.request.RegistrationRequest;
import com.dev.quikkkk.auth_service.dto.response.AuthenticationResponse;
import com.dev.quikkkk.auth_service.entity.Role;
import com.dev.quikkkk.auth_service.entity.User;
import com.dev.quikkkk.auth_service.exception.BusinessException;
import com.dev.quikkkk.auth_service.exception.ErrorCode;
import com.dev.quikkkk.auth_service.mapper.UserMapper;
import com.dev.quikkkk.auth_service.repository.IRoleRepository;
import com.dev.quikkkk.auth_service.repository.IUserRepository;
import com.dev.quikkkk.auth_service.service.IJwtService;
import com.dev.quikkkk.auth_service.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service Tests")
class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private IJwtService jwtService;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User testUser;
    private Role studentRole;
    private LoginRequest loginRequest;
    private RegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        studentRole = Role.builder()
                .id("role-id")
                .name("ROLE_STUDENT")
                .users(new HashSet<>())
                .build();

        testUser = User.builder()
                .id("user-id")
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .enabled(true)
                .locked(false)
                .expired(false)
                .emailVerified(false)
                .roles(Set.of(studentRole))
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        registrationRequest = RegistrationRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .firstName("New")
                .lastName("User")
                .password("Password123!")
                .confirmPassword("Password123!")
                .build();
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfully() {
        // Given
        when(userRepository.findByUsernameIgnoreCase(loginRequest.getUsername()))
                .thenReturn(Optional.of(testUser));
        when(jwtService.generateAccessToken(testUser)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(testUser)).thenReturn("refresh-token");

        // When
        AuthenticationResponse response = authenticationService.login(loginRequest);

        // Then
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsernameIgnoreCase(loginRequest.getUsername());
        verify(jwtService).generateAccessToken(testUser);
        verify(jwtService).generateRefreshToken(testUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found during login")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByUsernameIgnoreCase(loginRequest.getUsername()))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authenticationService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("User not found");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should throw exception when credentials are invalid")
    void shouldThrowExceptionWhenCredentialsInvalid() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        assertThatThrownBy(() -> authenticationService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userRepository, jwtService);
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("refresh-token");
        when(jwtService.refreshAccessToken(refreshRequest.getRefreshToken()))
                .thenReturn("new-access-token");

        // When
        AuthenticationResponse response = authenticationService.refreshToken(refreshRequest);

        // Then
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");

        verify(jwtService).refreshAccessToken(refreshRequest.getRefreshToken());
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        // Given
        when(userRepository.existsByUsernameIgnoreCase(registrationRequest.getUsername()))
                .thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase(registrationRequest.getEmail()))
                .thenReturn(false);
        when(roleRepository.findByName("ROLE_STUDENT"))
                .thenReturn(Optional.of(studentRole));
        when(mapper.toUser(registrationRequest)).thenReturn(testUser);

        // When
        authenticationService.register(registrationRequest);

        // Then
        verify(userRepository).existsByUsernameIgnoreCase(registrationRequest.getUsername());
        verify(userRepository).existsByEmailIgnoreCase(registrationRequest.getEmail());
        verify(roleRepository).findByName("ROLE_STUDENT");
        verify(mapper).toUser(registrationRequest);
        verify(userRepository).save(testUser);
        verify(roleRepository).save(studentRole);
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameExists() {
        // Given
        when(userRepository.existsByUsernameIgnoreCase(registrationRequest.getUsername()))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authenticationService.register(registrationRequest))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ErrorCode.USERNAME_ALREADY_EXISTS);

        verify(userRepository).existsByUsernameIgnoreCase(registrationRequest.getUsername());
        verifyNoMoreInteractions(userRepository, roleRepository, mapper);
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        when(userRepository.existsByUsernameIgnoreCase(registrationRequest.getUsername()))
                .thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase(registrationRequest.getEmail()))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authenticationService.register(registrationRequest))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);

        verify(userRepository).existsByEmailIgnoreCase(registrationRequest.getEmail());
        verifyNoInteractions(roleRepository, mapper);
    }

    @Test
    @DisplayName("Should throw exception when passwords don't match")
    void shouldThrowExceptionWhenPasswordsDontMatch() {
        // Given
        registrationRequest.setConfirmPassword("DifferentPassword123!");
        when(userRepository.existsByUsernameIgnoreCase(registrationRequest.getUsername()))
                .thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase(registrationRequest.getEmail()))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authenticationService.register(registrationRequest))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ErrorCode.PASSWORD_MISMATCH);

        verifyNoInteractions(roleRepository, mapper);
    }
}
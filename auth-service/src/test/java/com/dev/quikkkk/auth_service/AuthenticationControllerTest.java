package com.dev.quikkkk.auth_service;

import com.dev.quikkkk.auth_service.controller.AuthenticationController;
import com.dev.quikkkk.auth_service.dto.request.LoginRequest;
import com.dev.quikkkk.auth_service.dto.request.RefreshTokenRequest;
import com.dev.quikkkk.auth_service.dto.request.RegistrationRequest;
import com.dev.quikkkk.auth_service.dto.response.AuthenticationResponse;
import com.dev.quikkkk.auth_service.exception.BusinessException;
import com.dev.quikkkk.auth_service.exception.ErrorCode;
import com.dev.quikkkk.auth_service.service.IAuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@DisplayName("Authentication Controller Tests")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAuthenticationService authenticationService;

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        RegistrationRequest request = RegistrationRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("Password123!")
                .confirmPassword("Password123!")
                .build();

        doNothing().when(authenticationService).register(any(RegistrationRequest.class));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").doesNotExist());

        verify(authenticationService).register(any(RegistrationRequest.class));
    }

    @Test
    @DisplayName("Should handle registration validation errors")
    void shouldHandleRegistrationValidationErrors() throws Exception {
        // Given - Invalid request
        RegistrationRequest request = RegistrationRequest.builder()
                .username("") // Invalid - empty
                .email("invalid-email") // Invalid format
                .firstName("")
                .lastName("")
                .password("weak") // Invalid - too weak
                .confirmPassword("different")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationService);
    }

    @Test
    @DisplayName("Should handle business exception during registration")
    void shouldHandleBusinessExceptionDuringRegistration() throws Exception {
        // Given
        RegistrationRequest request = RegistrationRequest.builder()
                .username("existinguser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("Password123!")
                .confirmPassword("Password123!")
                .build();

        doThrow(new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS))
                .when(authenticationService).register(any(RegistrationRequest.class));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("USERNAME_ALREADY_EXISTS"));

        verify(authenticationService).register(any(RegistrationRequest.class));
    }

    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("Password123!")
                .build();

        AuthenticationResponse response = AuthenticationResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .build();

        when(authenticationService.login(any(LoginRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.access_token").value("access-token"))
                .andExpect(jsonPath("$.data.refresh_token").value("refresh-token"))
                .andExpect(jsonPath("$.data.token_type").value("Bearer"));

        verify(authenticationService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should handle login validation errors")
    void shouldHandleLoginValidationErrors() throws Exception {
        // Given - Invalid request
        LoginRequest request = LoginRequest.builder()
                .username("") // Invalid - empty
                .password("") // Invalid - empty
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationService);
    }

    @Test
    @DisplayName("Should handle bad credentials exception")
    void shouldHandleBadCredentialsException() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        when(authenticationService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("BAD_CREDENTIALS"));

        verify(authenticationService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() throws Exception {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");
        AuthenticationResponse response = AuthenticationResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .build();

        when(authenticationService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.access_token").value("new-access-token"))
                .andExpect(jsonPath("$.data.refresh_token").value("refresh-token"))
                .andExpect(jsonPath("$.data.token_type").value("Bearer"));

        verify(authenticationService).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    @DisplayName("Should handle invalid refresh token")
    void shouldHandleInvalidRefreshToken() throws Exception {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");

        when(authenticationService.refreshToken(any(RefreshTokenRequest.class)))
                .thenThrow(new RuntimeException("Invalid JWT token"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(authenticationService).refreshToken(any(RefreshTokenRequest.class));
    }
}


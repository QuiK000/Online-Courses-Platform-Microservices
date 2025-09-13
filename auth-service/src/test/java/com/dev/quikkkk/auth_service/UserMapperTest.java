package com.dev.quikkkk.auth_service;

import com.dev.quikkkk.auth_service.dto.request.RegistrationRequest;
import com.dev.quikkkk.auth_service.entity.User;
import com.dev.quikkkk.auth_service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Mapper Tests")
class UserMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;
    private RegistrationRequest registrationRequest;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper(passwordEncoder);

        registrationRequest = RegistrationRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("Password123!")
                .confirmPassword("Password123!")
                .build();
    }

    @Test
    @DisplayName("Should map registration request to user")
    void shouldMapRegistrationRequestToUser() {
        // Given
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");

        // When
        User user = userMapper.toUser(registrationRequest);

        // Then
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("Test");
        assertThat(user.getLastName()).isEqualTo("User");
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.isLocked()).isFalse();
        assertThat(user.isExpired()).isFalse();
        assertThat(user.isEmailVerified()).isFalse();
        assertThat(user.getRoles()).isEmpty();
    }
}
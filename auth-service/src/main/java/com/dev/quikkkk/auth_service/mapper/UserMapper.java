package com.dev.quikkkk.auth_service.mapper;

import com.dev.quikkkk.auth_service.dto.request.RegistrationRequest;
import com.dev.quikkkk.auth_service.dto.response.UserResponse;
import com.dev.quikkkk.auth_service.entity.UserCredentials;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserCredentials toUser(RegistrationRequest request) {
        return UserCredentials
                .builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .locked(false)
                .expired(false)
                .emailVerified(false)
                .roles(new HashSet<>())
                .build();
    }

    public UserResponse toUserResponse(UserCredentials userCredentials) {
        return UserResponse
                .builder()
                .id(userCredentials.getId())
                .username(userCredentials.getUsername())
                .email(userCredentials.getEmail())
                .firstName(userCredentials.getFirstName())
                .lastName(userCredentials.getLastName())
                .role(userCredentials.getRoles().iterator().next().getName())
                .build();
    }
}

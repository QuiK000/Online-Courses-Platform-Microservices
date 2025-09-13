package com.dev.quikkkk.auth_service.service;

import com.dev.quikkkk.auth_service.entity.User;

import java.util.List;

public interface IJwtService {
    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    String refreshAccessToken(String refreshToken);

    String extractUsername(String token);

    String extractUserId(String token);

    List<String> extractRoles(String token);

    boolean isTokenValid(String token, String expectedUsername);
}

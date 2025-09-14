package com.dev.quikkkk.auth_service.service;

import com.dev.quikkkk.auth_service.entity.UserCredentials;

import java.util.List;

public interface IJwtService {
    String generateAccessToken(UserCredentials userCredentials);

    String generateRefreshToken(UserCredentials userCredentials);

    String refreshAccessToken(String refreshToken);

    String extractUsername(String token);

    String extractUserId(String token);

    List<String> extractRoles(String token);

    boolean isTokenValid(String token, String expectedUsername);
}

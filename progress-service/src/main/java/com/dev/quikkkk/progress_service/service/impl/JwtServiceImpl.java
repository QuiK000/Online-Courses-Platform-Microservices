package com.dev.quikkkk.progress_service.service.impl;

import com.dev.quikkkk.progress_service.service.IJwtService;

import java.util.List;

public class JwtServiceImpl implements IJwtService {
    @Override
    public String extractUsername(String token) {
        return "";
    }

    @Override
    public String extractUserId(String token) {
        return "";
    }

    @Override
    public List<String> extractRoles(String token) {
        return List.of();
    }

    @Override
    public boolean isTokenValid(String token, String expectedUsername) {
        return false;
    }
}

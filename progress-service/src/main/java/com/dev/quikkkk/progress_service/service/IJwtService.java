package com.dev.quikkkk.progress_service.service;

import java.util.List;

public interface IJwtService {
    String extractUsername(String token);

    String extractUserId(String token);

    List<String> extractRoles(String token);

    boolean isTokenValid(String token, String expectedUsername);
}

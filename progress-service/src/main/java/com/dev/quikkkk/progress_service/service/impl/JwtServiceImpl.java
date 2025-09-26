package com.dev.quikkkk.progress_service.service.impl;

import com.dev.quikkkk.progress_service.exception.BusinessException;
import com.dev.quikkkk.progress_service.service.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utils.KeyUtils;

import java.security.PublicKey;
import java.util.Date;
import java.util.List;

import static com.dev.quikkkk.progress_service.exception.ErrorCode.INVALID_JWT_TOKEN;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements IJwtService {
    private static final String USER_ID = "userId";
    private static final String PATH_TO_PUBLIC_KEY = "keys/local-only/public_key.pem";

    private final PublicKey publicKey;

    public JwtServiceImpl() throws Exception {
        this.publicKey = KeyUtils.loadPublicKey(PATH_TO_PUBLIC_KEY);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    @Override
    public String extractUserId(String token) {
        return extractClaims(token).get(USER_ID).toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) extractClaims(token).get("roles");
    }

    @Override
    public boolean isTokenValid(String token, String expectedUsername) {
        String username = extractUsername(token);
        return username.equals(expectedUsername) && !isTokenExpired(token);
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new BusinessException(INVALID_JWT_TOKEN);
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }
}

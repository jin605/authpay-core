package com.jin605.authpaycore.auth.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtUtil jwtUtil;
    private final com.jin605.authpaycore.config.JwtProperties jwtProperties;

    public String createAccessToken(Long userId, String email, String role, String status) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);
        claims.put("status", status);
        claims.put("token_type", "access");

        return jwtUtil.createToken(String.valueOf(userId), claims, jwtProperties.getAccessTokenExpirationMs());
    }

    public String createRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("token_type", "refresh");

        return jwtUtil.createToken(String.valueOf(userId), claims, jwtProperties.getRefreshTokenExpirationMs());
    }

    public boolean isValidToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public Claims getClaims(String token) {
        return jwtUtil.parseClaims(token);
    }

    public String resolveToken(String bearerToken) {
        if (bearerToken == null || bearerToken.isBlank()) {
            return null;
        }
        if (!bearerToken.startsWith("Bearer ")) {
            return null;
        }
        return bearerToken.substring(7);
    }

    public Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    public String getTokenType(String token) {
        return getClaims(token).get("token_type", String.class);
    }


}

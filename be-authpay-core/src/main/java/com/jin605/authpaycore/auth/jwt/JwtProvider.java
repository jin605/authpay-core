package com.jin605.authpaycore.auth.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final String TOKEN_TYPE = "token_type";
    private static final String ACCESS = "access";
    private static final String REFRESH = "refresh";
    private static final String JTI = "jti";


    private final JwtUtil jwtUtil;
    private final com.jin605.authpaycore.config.JwtProperties jwtProperties;

    public String createAccessToken(Long userId, String email, String role, String status) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        claims.put("status", status);
        claims.put(TOKEN_TYPE, ACCESS);
        claims.put(JTI, UUID.randomUUID().toString());

        return jwtUtil.createToken(String.valueOf(userId), claims, jwtProperties.getAccessTokenExpirationMs());
    }

    public String createRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE, REFRESH);

        return jwtUtil.createToken(String.valueOf(userId), claims, jwtProperties.getRefreshTokenExpirationMs());
    }

    public boolean isValidToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public Claims getClaims(String token) {
        return jwtUtil.parseClaims(token);
    }

    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    public String getTokenType(String token) {
        return getClaims(token).get(TOKEN_TYPE, String.class);
    }

    public boolean isRefreshToken(String token) {
        return REFRESH.equals(getTokenType(token));
    }

    public boolean isAccessToken(String token) {
        return ACCESS.equals(getTokenType(token));
    }

    public String getJti (String token) {
        return getClaims(token).get(JTI, String.class);
    }
    public long getRemainingExpirationMs(String token) {
        return getClaims(token).getExpiration().getTime() - System.currentTimeMillis();
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

}

package com.jin605.authpaycore.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AccessTokenBlacklistService {

    private static final String BLACKLIST_KEY_PREFIX = "blacklist:access:";

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtProvider jwtProvider;

    public void blacklist(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) return;
        if (!jwtProvider.isValidToken(accessToken) || !jwtProvider.isAccessToken(accessToken)) return;

        String jti = jwtProvider.getJti(accessToken);
        if (jti == null || jti.isBlank()) return;

        long ttlMs = jwtProvider.getRemainingExpirationMs(accessToken);
        if (ttlMs <= 0) return;

        stringRedisTemplate.opsForValue().set(
                blacklistkey(jti),
                "1",
                Duration.ofMillis(ttlMs)
        );
    }

    public boolean isBlacklisted(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) return false;

        String jti = jwtProvider.getJti(accessToken);
        if (jti == null || jti.isBlank()) return false;

        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(blacklistkey(jti)));
    }

    private String blacklistkey(String jti) {

        return BLACKLIST_KEY_PREFIX + jti;
    }
}

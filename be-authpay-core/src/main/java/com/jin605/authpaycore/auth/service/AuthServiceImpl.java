package com.jin605.authpaycore.auth.service;

import com.jin605.authpaycore.auth.dto.LoginRequest;
import com.jin605.authpaycore.auth.dto.SignupRequest;
import com.jin605.authpaycore.auth.jwt.JwtProvider;
import com.jin605.authpaycore.config.JwtProperties;
import com.jin605.authpaycore.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String REFRESH_KEY_PREFIX = "refresh:";

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public AuthTokens signup(SignupRequest request) {
        return null;
    }

    @Override
    public AuthTokens login(LoginRequest request) {
        return null;
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        return null;
    }
}

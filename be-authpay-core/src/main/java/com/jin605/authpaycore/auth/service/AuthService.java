package com.jin605.authpaycore.auth.service;

import com.jin605.authpaycore.auth.dto.LoginRequest;
import com.jin605.authpaycore.auth.dto.SignupRequest;

public interface AuthService {
    AuthTokens signup(SignupRequest request);
    AuthTokens login(LoginRequest request);
    AuthTokens refresh(String refreshToken);

    record AuthTokens(
            String accessToken,
            String refreshToken,
            Long accessTokenExpiresInMs,
            Long userId,
            String email,
            String role,
            String status
    ) {}
}

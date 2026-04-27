package com.jin605.authpaycore.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTokens {
    private final String accessToken;
    private final String refreshToken;
    private final Long accessTokenExpiresInMs;
    private final Long userId;
    private final String email;
    private final String role;
    private final String status;

}

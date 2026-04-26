package com.jin605.authpaycore.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {

    private String type;
    private String accessToken;
    private Long accessTokenExpiresInMs;
    private Long userId;
    private String email;
    private String role;
    private String status;

}

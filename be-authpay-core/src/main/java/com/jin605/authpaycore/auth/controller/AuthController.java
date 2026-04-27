package com.jin605.authpaycore.auth.controller;

import com.jin605.authpaycore.auth.dto.ApiResponse;
import com.jin605.authpaycore.auth.dto.AuthTokens;
import com.jin605.authpaycore.auth.dto.LoginRequest;
import com.jin605.authpaycore.auth.dto.SignupRequest;
import com.jin605.authpaycore.auth.dto.TokenResponse;
import com.jin605.authpaycore.auth.service.AuthService;
import com.jin605.authpaycore.config.JwtProperties;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final AuthService authService;
    private final JwtProperties jwtProperties;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(@Valid @RequestBody SignupRequest request) {

        AuthTokens tokens = authService.signup(request);
        return successWithRefreshCookie(HttpStatus.CREATED, "회원 가입 완료", tokens);

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {

        AuthTokens tokens = authService.login(request);
        return successWithRefreshCookie(HttpStatus.OK, "로그인 완료", tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, defaultValue = "") String refreshToken) {

        AuthTokens tokens = authService.refresh(refreshToken);
    return successWithRefreshCookie(HttpStatus.OK, "토큰 재발급 완료", tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String bearerToken,
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, defaultValue = "") String refreshToken
    ) {

        authService.logout(bearerToken, refreshToken);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, createLogoutRefreshTokenCookie().toString())
                .build();
    }

    private ResponseEntity<ApiResponse<TokenResponse>> successWithRefreshCookie(
            HttpStatus status,
            String message,
            AuthTokens tokens) {

        ApiResponse<TokenResponse> body = ApiResponse.<TokenResponse>builder()
                .status(status.value())
                .errorCode(null)
                .message(message)
                .result(toTokenResponse(tokens))
                .build();

        return ResponseEntity
                .status(status)
                .header(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(tokens.getRefreshToken()).toString())
                .body(body);
    }

    private TokenResponse toTokenResponse(AuthTokens tokens) {
        return TokenResponse.builder()
                .type("Bearer")
                .accessToken(tokens.getAccessToken())
                .accessTokenExpiresInMs(tokens.getAccessTokenExpiresInMs())
                .userId(tokens.getUserId())
                .email(tokens.getEmail())
                .role(tokens.getRole())
                .status(tokens.getStatus())
                .build();
    }

    private ResponseCookie createRefreshTokenCookie (String refreshToken) {
        long maxAgeSeconds = Math.max(0, jwtProperties.getRefreshTokenExpirationMs() / 1000);

        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }

    private ResponseCookie createLogoutRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
    }

















}

package com.jin605.authpaycore.auth.service;

import com.jin605.authpaycore.auth.dto.LoginRequest;
import com.jin605.authpaycore.auth.dto.SignupRequest;
import com.jin605.authpaycore.auth.jwt.JwtProvider;
import com.jin605.authpaycore.config.JwtProperties;
import com.jin605.authpaycore.user.mapper.UserMapper;
import com.jin605.authpaycore.user.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Locale;
import java.util.Objects;

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

        String email = normalizeEmail(request.getEmail());

        if (userMapper.findByEmail(email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        if (userMapper.findByNickname(email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName().trim())
                .nickname(request.getNickname().trim())
                .role("USER")
                .status("ACTIVE")
                .build();

        userMapper.insert(user);
        return issueTokens(user);
    }

    @Override
    public AuthTokens login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());
        User user = userMapper.findByEmail(email);

        if (user == null || user.getPasswordHash() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "활성 상태 사용자가 아닙니다.");
        }

        return issueTokens(user);
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 없습니다.");
        }
        if (!jwtProvider.isValidToken(refreshToken) || !jwtProvider.isRefreshToken(refreshToken)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다.");
        }

        Long userId = jwtProvider.getUserId(refreshToken);
        String stored = stringRedisTemplate.opsForValue().get(refreshKey(userId));

        if (!Objects.equals(stored, refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다.");
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(
                user.getId(), user.getEmail(), user.getRole(), user.getStatus());

        return new AuthTokens(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpirationMs(),
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }

    private AuthTokens issueTokens(User user) {
        String accessToken = jwtProvider.createAccessToken(
                user.getId(), user.getEmail(), user.getRole(), user.getStatus());

        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        stringRedisTemplate.opsForValue().set(
                refreshKey(user.getId()),
                refreshToken,
                Duration.ofMillis(jwtProperties.getRefreshTokenExpirationMs())
        );

        return new AuthTokens(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpirationMs(),
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }

    private String refreshKey(Long userId) {
        return REFRESH_KEY_PREFIX + userId;
    }


    private String normalizeEmail(@NotBlank @Email String email) {

        return String.valueOf(email).trim().toLowerCase(Locale.ROOT);

    }
}

package com.jin605.authpaycore.file.controller;

import com.jin605.authpaycore.auth.dto.ApiResponse;
import com.jin605.authpaycore.file.dto.CreatePresignedUploadUrlRequest;
import com.jin605.authpaycore.file.dto.CreatePresignedUploadUrlResponse;
import com.jin605.authpaycore.file.service.FileService;
import io.lettuce.core.dynamic.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/presigned-upload")
    public ResponseEntity<ApiResponse<CreatePresignedUploadUrlResponse>> createPresignedUploadUrl(
            Authentication authentication,
            @Value @RequestBody CreatePresignedUploadUrlRequest request
            ) {

        Long userId = extractUserId(authentication);
        CreatePresignedUploadUrlResponse result = fileService.createPresignedUploadUrl(userId, request);

        return ApiResponse.of(HttpStatus.OK, null, "Presigned upload URL 발급 완료", result);

    }


    private Long extractUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Long userId) {
            return userId;
        }

        if (principal instanceof String str && str.matches("\\d+")) {
            return Long.parseLong(str);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 사용자 정보를 확인할 수 없습니다.");
    }

}

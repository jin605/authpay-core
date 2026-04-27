package com.jin605.authpaycore.file.service;

import com.jin605.authpaycore.config.R2Properties;
import com.jin605.authpaycore.file.dto.CreatePresignedUploadUrlRequest;
import com.jin605.authpaycore.file.dto.CreatePresignedUploadUrlResponse;
import com.jin605.authpaycore.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif",
            "image/jpg"
    );

    private final S3Presigner s3Presigner;
    private final R2Properties r2Properties;
    private final UserMapper userMapper;

    @Override
    public CreatePresignedUploadUrlResponse createPresignedUploadUrl(Long userId, CreatePresignedUploadUrlRequest request) {

        validateRequest(request);

        String fileKey = generateFileKey(userId, request.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(r2Properties.getBucket())
                    .key(fileKey)
                    .contentType(request.getContentType())
                    .build();

            long durationMinutes = r2Properties.getPresignedUrlDurationMinutes() == null
                    ? 10L
                    : r2Properties.getPresignedUrlDurationMinutes();

            Duration signatureDuration = Duration.ofMinutes(durationMinutes);

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(signatureDuration)
                    .putObjectRequest(putObjectRequest)
                    .build();
            PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);

            return CreatePresignedUploadUrlResponse.builder()
                    .uploadUrl(presigned.url().toString())
                    .fileKey(fileKey)
                    .publicUrl(buildPublicUrl(fileKey))
                    .method("PUT")
                    .expiresInSeconds(signatureDuration.toSeconds())
                    .build();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "R2 presigned URL 발급 실패");
        }
    }

    @Override
    public String saveProfileImage(Long userId, String fileKey) {
        if (fileKey == null || fileKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fileKey는 필수입니다.");
        }

        String expectedPrefix = "temp/users/" + userId + "/";
        if (!fileKey.startsWith(expectedPrefix)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인 업로드 파일만 저장할 수 있습니다.");
        }

        String imageUrl = buildPublicUrl(fileKey);

        int updated = userMapper.updateImageUrl(userId, imageUrl);
        if (updated != 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        return imageUrl;
    }

    private void validateRequest(CreatePresignedUploadUrlRequest request) {
        if (request == null
                || request.getOriginalFilename() == null
                || request.getOriginalFilename().isBlank()
                || request.getContentType() == null
                || request.getContentType().isBlank()
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일 업로드 요청이 올바르지 않습니다.");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(request.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원하지 않은 파일 형식입니다.");
        }

        if (!request.getOriginalFilename().contains(".")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "확장자가 없는 파일명입니다.");
        }
    }

    private String generateFileKey(Long userId, String originalFilename) {
        String extension = extractExtension(originalFilename);
        LocalDate today = LocalDate.now();

        return String.format(
                "temp/users/%d/%d/%02d/%02d/%s.%s",
                userId,
                today.getYear(),
                today.getMonthValue(),
                today.getDayOfMonth(),
                UUID.randomUUID(),
                extension
        );
    }

    private String extractExtension(String originalFilename) {
        int idx = originalFilename.lastIndexOf(".");
        if (idx < 0 || idx == originalFilename.length() -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일 확장자를 확인해주세요.");
        }
        return originalFilename.substring(idx + 1).toLowerCase(Locale.ROOT);
    }

    private String buildPublicUrl(String fileKey) {
        String base = r2Properties.getPublicBaseUrl();
        if (base == null || base.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "R2 public base URL 설정이 없습니다.");
        }

        return base.endsWith("/") ? base + fileKey : base + "/" + fileKey;
    }
}

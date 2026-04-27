package com.jin605.authpaycore.file.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePresignedUploadUrlResponse {
    private String uploadUrl;
    private String fileKey;
    private String publicUrl;
    private String method;
    private Long expiresInSeconds;
}

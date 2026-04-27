package com.jin605.authpaycore.file.service;

import com.jin605.authpaycore.file.dto.CreatePresignedUploadUrlRequest;
import com.jin605.authpaycore.file.dto.CreatePresignedUploadUrlResponse;

public interface FileService {

    CreatePresignedUploadUrlResponse createPresignedUploadUrl (Long userId, CreatePresignedUploadUrlRequest request);
    String saveProfileImage(Long userId, String fileKey);
}

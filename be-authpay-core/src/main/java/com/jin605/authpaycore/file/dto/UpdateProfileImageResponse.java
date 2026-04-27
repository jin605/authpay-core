package com.jin605.authpaycore.file.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProfileImageResponse {
    private Long userId;
    private String imageUrl;
}

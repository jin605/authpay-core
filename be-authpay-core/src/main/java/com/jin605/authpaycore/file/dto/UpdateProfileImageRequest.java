package com.jin605.authpaycore.file.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileImageRequest {
    @NotBlank
    private String fileKey;
}

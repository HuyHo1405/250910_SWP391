package com.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthResponse dùng để trả về access token, refresh token
 * và thông tin cơ bản của user sau khi đăng nhập.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresInSec;  // đơn vị: giây
    private String tokenType;

    private UserProfileResponse user;

    @Builder.Default
    private boolean requiresVerification = false;
    private String message;
}
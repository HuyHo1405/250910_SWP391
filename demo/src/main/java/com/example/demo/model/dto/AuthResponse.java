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

    @NotBlank(message = "Access token cannot be blank")
    private String accessToken;

    @NotBlank(message = "Refresh token cannot be blank")
    private String refreshToken;

    @NotNull(message = "ExpiresIn cannot be null")
    private Long expiresIn;  // đơn vị: giây

    @NotBlank(message = "Token type cannot be blank")
    private String tokenType;

    @NotNull(message = "User info cannot be null")
    private UserInfo user;

    @Builder.Default
    private boolean requiresVerification = false;
    
    private String message;

    // Nested DTO cho user
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        @NotNull(message = "User ID cannot be null")
        private Long id;

        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Full name cannot be blank")
        private String fullName;

        @NotBlank(message = "Phone number cannot be blank")
        private String phoneNumber;

        @NotBlank(message = "Role cannot be blank")
        private String role;
        
        @NotBlank(message = "Status cannot be blank")
        private String status;
        
        @NotNull(message = "Created at cannot be null")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private java.time.LocalDateTime createdAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private java.time.LocalDateTime lastLogin;
    }
}
package com.example.demo.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthRequest {

    @Data
    public static class Register {
        @NotBlank(message = "Full name cannot be blank")
        private String fullName;

        @NotBlank(message = "Email address cannot be blank")
        @Email(message = "Email address must be valid")
        private String emailAddress;

        @NotBlank(message = "Phone number cannot be blank")
        @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Phone number must be valid")
        private String phoneNumber;

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        private String password;
    }

    @Data
    public static class Login {
        @NotBlank(message = "Email address cannot be blank")
        @Email(message = "Email address must be valid")
        private String userName;

        @NotBlank(message = "Password cannot be blank")
        private String password;
    }

    @Data
    public static class Logout {
        @NotBlank(message = "Refresh token cannot be blank")
        private String refreshToken;
    }

    @Data
    public static class Verify {
        @NotBlank(message = "Email address cannot be blank")
        @Email(message = "Email address must be valid")
        private String userName;

        @NotBlank(message = "Verification code cannot be blank")
        private String code;
    }

    @Data
    public static class ForgotPassword {
        @Email(message = "Email address must be valid")
        private String emailAddress;
    }

    @Data
    public static class ResetPassword {
        @NotBlank(message = "Token cannot be blank")
        private String token;

        @NotBlank(message = "New password cannot be blank")
        @Size(min = 6, message = "New password must be at least 6 characters long")
        private String newPassword;
    }
}


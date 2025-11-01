package com.example.demo.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthRequest {

    @Data
    public static class Register {
        @NotBlank(message = "Họ tên không được để trống")
        private String fullName;

        @NotBlank(message = "Địa chỉ email không được để trống")
        @Email(message = "Địa chỉ email không hợp lệ")
        private String emailAddress;

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Số điện thoại không hợp lệ")
        private String phoneNumber;

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
        private String password;
    }

    @Data
    public static class Login {
        @NotBlank(message = "Địa chỉ email không được để trống")
        @Email(message = "Địa chỉ email không hợp lệ")
        private String userName;

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
        private String password;
    }

    @Data
    public static class Logout {
        @NotBlank(message = "Refresh token không được để trống")
        private String refreshToken;
    }

    @Data
    public static class Verify {
        @NotBlank(message = "Địa chỉ email không được để trống")
        @Email(message = "Địa chỉ email không hợp lệ")
        private String userName;

        @NotBlank(message = "Mã xác thực không được để trống")
        private String code;
    }

    @Data
    public static class ForgotPassword {
        @NotBlank(message = "Email address cannot be blank")
        @Email(message = "Email address must be valid")
        private String emailAddress;
    }

    @Data
    public static class ResetPassword {
        @NotBlank(message = "Token không được để trống")
        private String token;

        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự")
        private String newPassword;
    }

    @Data
    public static class RefreshToken {
        @NotBlank(message = "Refresh token không được để trống")
        private String refreshToken;
    }
}


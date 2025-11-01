package com.example.demo.exception;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;

public class AuthException extends BaseServiceException {
    public AuthException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // ================================
    // AUTHENTICATION
    // ================================

    public static class InvalidCredentials extends AuthException {
        public InvalidCredentials() {
            super("INVALID_CREDENTIALS", "Tên đăng nhập hoặc mật khẩu không hợp lệ", HttpStatus.UNAUTHORIZED);
        }
    }

    public static class InvalidToken extends AuthException {
        public InvalidToken() {
            super("INVALID_TOKEN", "Token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED);
        }
    }

    public static class TokenExpired extends AuthException {
        public TokenExpired() {
            super("TOKEN_EXPIRED", "Token đã hết hạn", HttpStatus.UNAUTHORIZED);
        }
    }

    // ================================
    // VERIFICATION CODE
    // ================================

    public static class CodeExpired extends AuthException {
        public CodeExpired() {
            super("CODE_EXPIRED", "Mã xác thực đã hết hạn", HttpStatus.BAD_REQUEST);
        }
    }

    public static class CodeInvalid extends AuthException {
        public CodeInvalid() {
            super("CODE_INVALID", "Mã xác thực không hợp lệ", HttpStatus.BAD_REQUEST);
        }
    }

    public static class CodeGenerationFailed extends AuthException {
        public CodeGenerationFailed() {
            super("CODE_GENERATION_FAILED", "Tạo mã xác thực thất bại", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    // ================================
    // EMAIL
    // ================================

    public static class EmailSendFailed extends AuthException {
        public EmailSendFailed() {
            super("EMAIL_SEND_FAILED", "Gửi email thất bại", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    // ================================
    // ACCOUNT STATUS
    // ================================

    public static class AccountUnverified extends AuthException {
        public AccountUnverified() {
            super("ACCOUNT_UNVERIFIED", "Vui lòng xác thực email để tiếp tục", HttpStatus.FORBIDDEN);
        }
    }

    public static class AccountBlocked extends AuthException {
        public AccountBlocked() {
            super("ACCOUNT_BLOCKED", "Tài khoản đã bị khóa", HttpStatus.FORBIDDEN);
        }
    }
}

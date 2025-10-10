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
            super("INVALID_CREDENTIALS", "Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    public static class InvalidToken extends AuthException {
        public InvalidToken() {
            super("INVALID_TOKEN", "Token is invalid or expired", HttpStatus.UNAUTHORIZED);
        }
    }

    public static class TokenExpired extends AuthException {
        public TokenExpired() {
            super("TOKEN_EXPIRED", "Token has expired", HttpStatus.UNAUTHORIZED);
        }
    }

    // ================================
    // VERIFICATION CODE
    // ================================

    public static class CodeExpired extends AuthException {
        public CodeExpired() {
            super("CODE_EXPIRED", "Verification code has expired", HttpStatus.BAD_REQUEST);
        }
    }

    public static class CodeInvalid extends AuthException {
        public CodeInvalid() {
            super("CODE_INVALID", "Verification code is invalid", HttpStatus.BAD_REQUEST);
        }
    }

    public static class CodeGenerationFailed extends AuthException {
        public CodeGenerationFailed() {
            super("CODE_GENERATION_FAILED", "Failed to generate verification code", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    // ================================
    // EMAIL
    // ================================

    public static class EmailSendFailed extends AuthException {
        public EmailSendFailed() {
            super("EMAIL_SEND_FAILED", "Failed to send email", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    // ================================
    // ACCOUNT STATUS
    // ================================

    public static class AccountUnverified extends AuthException {
        public AccountUnverified() {
            super("ACCOUNT_UNVERIFIED", "Please verify your email", HttpStatus.FORBIDDEN);
        }
    }

    public static class AccountBlocked extends AuthException {
        public AccountBlocked() {
            super("ACCOUNT_BLOCKED", "Account is blocked", HttpStatus.FORBIDDEN);
        }
    }
}

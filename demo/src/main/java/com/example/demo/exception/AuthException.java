package com.example.demo.exception;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;

public class AuthException extends BaseServiceException {
    public AuthException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // Token & Credentials
    public static class InvalidCredentials extends AuthException {
        public InvalidCredentials() {
            super("INVALID_CREDENTIALS", "Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
    }

    public static class InvalidToken extends AuthException {
        public InvalidToken() {
            super("INVALID_TOKEN", "Token is invalid or expired", HttpStatus.BAD_REQUEST);
        }
    }

    public static class TokenExpired extends AuthException {
        public TokenExpired() {
            super("TOKEN_EXPIRED", "Token has expired", HttpStatus.UNAUTHORIZED);
        }
    }

    // Verification code
    public static class CodeGenerationFailed extends AuthException {
        public CodeGenerationFailed() {
            super("CODE_GENERATION_FAILED", "Failed to generate verification code", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public static class VerificationCodeExpired extends AuthException {
        public VerificationCodeExpired() {
            super("CODE_EXPIRED", "Verification code has expired", HttpStatus.BAD_REQUEST);
        }
    }

    public static class VerificationCodeInvalid extends AuthException {
        public VerificationCodeInvalid() {
            super("CODE_INVALID", "Verification code is invalid", HttpStatus.BAD_REQUEST);
        }
    }

    // Mail
    public static class EmailSendFailed extends AuthException {
        public EmailSendFailed() {
            super("EMAIL_SEND_FAILED", "Failed to send email", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    // Account status
    public static class UnverifiedAccount extends AuthException {
        public UnverifiedAccount() {
            super("UNVERIFIED_ACCOUNT", "Please verify your email address", HttpStatus.FORBIDDEN);
        }
    }

    public static class AccountBlocked extends AuthException {
        public AccountBlocked() {
            super("ACCOUNT_BLOCKED", "This account is blocked", HttpStatus.FORBIDDEN);
        }
    }

}

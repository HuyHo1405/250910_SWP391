package com.example.demo.exception;

public class AuthException extends RuntimeException {
    private final String code;

    public AuthException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static class UserNotFoundException extends AuthException {
        public UserNotFoundException() {
            super("User not found", "USER_NOT_FOUND");
        }
    }

    public static class InvalidCredentialsException extends AuthException {
        public InvalidCredentialsException() {
            super("Invalid password", "INVALID_CREDENTIALS");
        }
    }

    public static class EmailAlreadyExistsException extends AuthException {
        public EmailAlreadyExistsException() {
            super("Email Address already exists", "EMAIL_EXISTS");
        }
    }

    public static class PhoneNumberAlreadyExistsException extends AuthException {
        public PhoneNumberAlreadyExistsException() {
            super("Phone Number already exists", "PHONE_EXISTS");
        }
    }

    public static class UnverifiedAccountException extends AuthException {
        public UnverifiedAccountException() {
            super("Please verify your email address. A new verification email has been sent.", "UNVERIFIED_ACCOUNT");
        }
    }

    public static class InvalidTokenException extends AuthException {
        public InvalidTokenException(String message) {
            super(message, "INVALID_TOKEN");
        }
    }

    public static class EmailSendFailedException extends AuthException {
        public EmailSendFailedException() {
            super("Failed to send verification email", "EMAIL_SEND_FAILED");
        }
    }
}

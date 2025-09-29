package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class UserException extends BaseServiceException {
    public UserException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    public static class UserNotFound extends UserException {
        public UserNotFound() {
            super("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND);
        }
    }

    public static class RoleNotFound extends UserException {
        public RoleNotFound() {
            super("ROLE_NOT_FOUND", "Role not found or invalid", HttpStatus.BAD_REQUEST);
        }
    }

    public static class UserAlreadyExists extends UserException {
        public UserAlreadyExists() {
            super("USER_EXISTS", "User already exists", HttpStatus.CONFLICT);
        }
    }

    public static class EmailAlreadyExists extends UserException {
        public EmailAlreadyExists() {
            super("EMAIL_ALREADY_EXISTS", "This email is already registered", HttpStatus.CONFLICT);
        }
    }

    public static class PhoneAlreadyExists extends UserException {
        public PhoneAlreadyExists() {
            super("PHONE_ALREADY_EXISTS", "This phone number is already registered", HttpStatus.CONFLICT);
        }
    }

}

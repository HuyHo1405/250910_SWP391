package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class CommonException extends BaseServiceException {
    public CommonException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // ================================
    // NOT FOUND - Dùng khi resource không tồn tại
    // ================================

    public static class NotFound extends CommonException {
        public NotFound(String resource, Object identifier) {
            super(
                    "NOT_FOUND",
                    String.format("%s not found: %s", resource, identifier),
                    HttpStatus.NOT_FOUND
            );
        }

        public NotFound(String message) {
            super("NOT_FOUND", message, HttpStatus.NOT_FOUND);
        }
    }

    // ================================
    // ALREADY EXISTS - Dùng khi resource đã tồn tại
    // ================================

    public static class AlreadyExists extends CommonException {
        public AlreadyExists(String resource, String field, Object value) {
            super(
                    "ALREADY_EXISTS",
                    String.format("%s already exists with %s: %s", resource, field, value),
                    HttpStatus.CONFLICT
            );
        }

        public AlreadyExists(String message) {
            super("ALREADY_EXISTS", message, HttpStatus.CONFLICT);
        }
    }

    // ================================
    // INVALID OPERATION - Dùng cho validation/business rules
    // ================================

    public static class InvalidOperation extends CommonException {
        public InvalidOperation(String code, String message) {
            super(code, message, HttpStatus.BAD_REQUEST);
        }

        public InvalidOperation(String message) {
            super("INVALID_OPERATION", message, HttpStatus.BAD_REQUEST);
        }
    }

    // ================================
    // FORBIDDEN - Dùng cho unauthorized access
    // ================================

    public static class Forbidden extends CommonException {
        public Forbidden(String message) {
            super("FORBIDDEN", message, HttpStatus.FORBIDDEN);
        }

        public Forbidden() {
            super("FORBIDDEN", "You are not authorized to perform this action", HttpStatus.FORBIDDEN);
        }
    }

    // ================================
    // CONFLICT - Dùng cho resource conflicts
    // ================================

    public static class Conflict extends CommonException {
        public Conflict(String code, String message) {
            super(code, message, HttpStatus.CONFLICT);
        }

        public Conflict(String message) {
            super("CONFLICT", message, HttpStatus.CONFLICT);
        }
    }

    // ================================
    // SERVICE UNAVAILABLE - Dùng cho external service errors
    // ================================

    public static class ServiceUnavailable extends CommonException {
        public ServiceUnavailable(String message) {
            super("SERVICE_UNAVAILABLE", message, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}

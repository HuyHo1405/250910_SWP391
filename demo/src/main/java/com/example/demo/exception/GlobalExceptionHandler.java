package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handleAuthException(AuthException ex) {
        log.error("Auth error occurred: {} - {}", ex.getCode(), ex.getMessage());
        return ResponseEntity
                .status(determineHttpStatus(ex))
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred"));
    }

    private HttpStatus determineHttpStatus(AuthException ex) {
        return switch (ex.getCode()) {
            case "USER_NOT_FOUND", "INVALID_CREDENTIALS" -> HttpStatus.UNAUTHORIZED;
            case "EMAIL_EXISTS", "PHONE_EXISTS" -> HttpStatus.CONFLICT;
            case "UNVERIFIED_ACCOUNT" -> HttpStatus.FORBIDDEN;
            case "INVALID_TOKEN" -> HttpStatus.BAD_REQUEST;
            case "EMAIL_SEND_FAILED" -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}

class ErrorResponse {
    private final String code;
    private final String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

package com.example.demo.exception;

import com.example.demo.model.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            WebRequest request) {

        String message = "Data integrity violation";
        if (ex.getCause() instanceof ConstraintViolationException cve) {
            message = cve.getMessage();
        }

        log.warn("Data integrity violation: {}", message);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "DATA_INTEGRITY_VIOLATION",
                        message,
                        LocalDateTime.now(),
                        request.getDescription(false)
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            WebRequest request) {

        log.warn("Access denied: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        "ACCESS_DENIED",
                        "You don't have permission to perform this action",
                        LocalDateTime.now(),
                        request.getDescription(false)
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        String errorMsg = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                .orElse("Invalid input");

        log.warn("Validation failed: {}", errorMsg);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "INVALID_INPUT",
                        errorMsg,
                        LocalDateTime.now(),
                        request.getDescription(false)
                ));
    }

    // Handle tất cả DomainException (CommonException, AuthException, VehicleException, BookingException, etc.)
    @ExceptionHandler(BaseServiceException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            BaseServiceException ex,
            WebRequest request) {

        log.error("Domain error occurred: {} - {}", ex.getCode(), ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ErrorResponse(
                        ex.getCode(),
                        ex.getMessage(),
                        LocalDateTime.now(),
                        request.getDescription(false)
                ));
    }

    // Catch-all cho exception bất ngờ
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error occurred: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "INTERNAL_ERROR",
                        "An unexpected error occurred",
                        LocalDateTime.now(),
                        request.getDescription(false)
                ));
    }
}



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
    public ResponseEntity handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            WebRequest request) {
        String message = "Vi phạm tính toàn vẹn dữ liệu";
        if (ex.getCause() instanceof ConstraintViolationException cve) {
            message = cve.getMessage();
        }

        log.warn("Vi phạm tính toàn vẹn dữ liệu: {}", message);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "DATA_INTEGRITY_VIOLATION",
                        message,
                        LocalDateTime.now(),
                        request.getDescription(false)
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity handleAccessDenied(
            AccessDeniedException ex,
            WebRequest request) {
        log.warn("Truy cập bị từ chối: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        "ACCESS_DENIED",
                        "Bạn không có quyền thực hiện hành động này",
                        LocalDateTime.now(),
                        request.getDescription(false)
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        String errorMsg = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                .orElse("Dữ liệu nhập không hợp lệ");
        log.warn("Xác thực thất bại: {}", errorMsg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "INVALID_INPUT",
                        errorMsg,
                        LocalDateTime.now(),
                        request.getDescription(false)
                ));
    }

    // Xử lý tất cả DomainException (CommonException, AuthException, VehicleException, BookingException, etc.)
    @ExceptionHandler(BaseServiceException.class)
    public ResponseEntity handleDomainException(
            BaseServiceException ex,
            WebRequest request) {
        log.error("Lỗi miền xảy ra: {} - {}", ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ErrorResponse(
                        ex.getCode(),
                        ex.getMessage(),
                        LocalDateTime.now(),
                        request.getDescription(false)
                ));
    }

    // Xử lý tất cả exception bất ngờ
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleAllExceptions(
            Exception ex,
            WebRequest request) {
        log.error("Lỗi bất ngờ xảy ra: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "INTERNAL_ERROR",
                        "Đã xảy ra lỗi bất ngờ",
                        LocalDateTime.now(),
                        request.getDescription(false)
                ));
    }
}

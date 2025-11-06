package com.example.demo.exception;

import com.example.demo.model.dto.ErrorResponse; // Giả sử ErrorResponse của bạn ở đây
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException; // Đảm bảo bạn import đúng
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. XỬ LÝ LỖI 400 - JSON SAI CÚ PHÁP HOẶC THỪA TRƯỜNG
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex,
            WebRequest request) {

        Throwable cause = ex.getCause();
        String code = "INVALID_REQUEST_BODY";
        String message = "Request body không hợp lệ hoặc sai cú pháp"; // Message mặc định

        if (cause instanceof UnrecognizedPropertyException upe) {
            // Trường hợp: Client gửi thừa trường (như ta vừa bàn)
            message = "Trường không xác định (unrecognized field): '" + upe.getPropertyName() + "'";
            code = "UNRECOGNIZED_FIELD";
            log.warn("API_BAD_REQUEST: Client gửi thừa trường '{}' tại {}",
                    upe.getPropertyName(), upe.getLocation());

        } else if (cause instanceof JsonParseException jpe) {
            // Trường hợp: JSON sai cú pháp (thiếu ngoặc, thừa dấu phẩy...)
            message = "Cú pháp JSON không hợp lệ: " + jpe.getOriginalMessage();
            code = "INVALID_JSON_SYNTAX";
            log.warn("API_BAD_REQUEST: JSON body bị sai cú pháp", jpe);
        } else {
            // Các trường hợp không đọc được message khác
            log.warn("API_BAD_REQUEST: Không thể đọc request body", ex);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        code,
                        message,
                        LocalDateTime.now(),
                        request.getDescription(false)
                ));
    }

    // 2. XỬ LÝ LỖI 400 - VALIDATION (DTO CÓ @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
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

    // 3. XỬ LÝ LỖI 403 - KHÔNG CÓ QUYỀN
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
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

    // 4. XỬ LÝ LỖI 409 - XUNG ĐỘT DATABASE
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
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

    // 5. XỬ LÝ TẤT CẢ LỖI NGHIỆP VỤ (CUSTOM EXCEPTION)
    @ExceptionHandler(BaseServiceException.class) // Giả sử đây là class cha của các Exception nghiệp vụ
    public ResponseEntity<ErrorResponse> handleDomainException(
            BaseServiceException ex,
            WebRequest request) {
        log.error("Lỗi nghiệp vụ xảy ra: {} - {}", ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ErrorResponse(
                        ex.getCode(),
                        ex.getMessage(),
                        LocalDateTime.now(),
                        request.getDescription(false)
                ));
    }

    // 6. XỬ LÝ TẤT CẢ LỖI 500 (BẤT NGỜ)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(
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
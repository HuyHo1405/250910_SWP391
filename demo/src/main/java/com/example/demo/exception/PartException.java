package com.example.demo.exception;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public class PartException extends BaseServiceException {

    public PartException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // ================================
    // NOT FOUND
    // ================================

    public static class PartNotFound extends PartException {
        public PartNotFound(Long id) {
            super("PART_NOT_FOUND",
                    String.format("Phụ tùng với ID %d không tìm thấy", id),
                    HttpStatus.NOT_FOUND);
        }
    }

    public static class PartNumberNotFound extends PartException {
        public PartNumberNotFound(String partNumber) {
            super("PART_NUMBER_NOT_FOUND",
                    String.format("Phụ tùng với mã số %s không tìm thấy", partNumber),
                    HttpStatus.NOT_FOUND);
        }
    }

    // ================================
    // CONFLICT / DUPLICATE
    // ================================

    public static class PartNumberExists extends PartException {
        public PartNumberExists(String partNumber) {
            super("PART_NUMBER_EXISTS",
                    String.format("Mã số phụ tùng %s đã tồn tại trong hệ thống", partNumber),
                    HttpStatus.CONFLICT);
        }
    }

    // ================================
    // VALIDATION / INVALID REQUEST
    // ================================

    public static class InvalidPrice extends PartException {
        public InvalidPrice() {
            super("INVALID_PRICE",
                    "Giá phụ tùng phải lớn hơn hoặc bằng 0",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static class InvalidQuantity extends PartException {
        public InvalidQuantity() {
            super("INVALID_QUANTITY",
                    "Số lượng phụ tùng phải lớn hơn hoặc bằng 0",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static class InvalidStockAdjustment extends PartException {
        public InvalidStockAdjustment(String reason) {
            super("INVALID_STOCK_ADJUSTMENT",
                    String.format("Điều chỉnh kho không hợp lệ: %s", reason),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static class NegativeQuantityResult extends PartException {
        public NegativeQuantityResult(BigDecimal currentQuantity, BigDecimal adjustment) {
            super("NEGATIVE_QUANTITY_RESULT",
                    String.format("Điều chỉnh " + currentQuantity +" sẽ dẫn đến số lượng âm (hiện tại: " + adjustment + ")"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static class InvalidAmount extends PartException {
        public InvalidAmount(String operation) {
            super("INVALID_AMOUNT",
                    String.format("Số tiền cho thao tác %s phải dương", operation),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // ================================
    // INVENTORY / STOCK
    // ================================

    public static class InsufficientStock extends PartException {
        public InsufficientStock(Long partId, int available, int required) {
            super("INSUFFICIENT_STOCK",
                    String.format("Không đủ kho cho phụ tùng ID %d. Có sẵn: %d, Cần: %d",
                            partId, available, required),
                    HttpStatus.CONFLICT);
        }
    }

    public static class PartOutOfStock extends PartException {
        public PartOutOfStock(Long partId, String partName) {
            super("PART_OUT_OF_STOCK",
                    String.format("Phụ tùng '%s' (ID: %d) hết hàng", partName, partId),
                    HttpStatus.CONFLICT);
        }
    }

    public static class PartInactive extends PartException {
        public PartInactive(Long partId) {
            super("PART_INACTIVE",
                    String.format("Phụ tùng với ID %d không hoạt động và không thể sử dụng", partId),
                    HttpStatus.CONFLICT);
        }
    }

    public static class PartUnavailable extends PartException {
        public PartUnavailable(Long partId, String reason) {
            super("PART_UNAVAILABLE",
                    String.format("Phụ tùng với ID %d không có sẵn: %s", partId, reason),
                    HttpStatus.CONFLICT);
        }
    }

    // ================================
    // BUSINESS LOGIC
    // ================================

    public static class CannotDeletePart extends PartException {
        public CannotDeletePart(Long partId, String reason) {
            super("CANNOT_DELETE_PART",
                    String.format("Không thể xóa phụ tùng ID %d: %s", partId, reason),
                    HttpStatus.CONFLICT);
        }
    }

    public static class PartAlreadyInactive extends PartException {
        public PartAlreadyInactive(Long partId) {
            super("PART_ALREADY_INACTIVE",
                    String.format("Phụ tùng với ID %d đã không hoạt động", partId),
                    HttpStatus.CONFLICT);
        }
    }

    public static class PartAlreadyActive extends PartException {
        public PartAlreadyActive(Long partId) {
            super("PART_ALREADY_ACTIVE",
                    String.format("Phụ tùng với ID %d đã hoạt động", partId),
                    HttpStatus.CONFLICT);
        }
    }
}

package com.example.demo.exception;

import org.springframework.http.HttpStatus;

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
                    String.format("Part with ID %d not found", id),
                    HttpStatus.NOT_FOUND);
        }
    }

    public static class PartNumberNotFound extends PartException {
        public PartNumberNotFound(Integer partNumber) {
            super("PART_NUMBER_NOT_FOUND",
                    String.format("Part with part number %d not found", partNumber),
                    HttpStatus.NOT_FOUND);
        }
    }

    // ================================
    // CONFLICT / DUPLICATE
    // ================================

    public static class PartNumberExists extends PartException {
        public PartNumberExists(Integer partNumber) {
            super("PART_NUMBER_EXISTS",
                    String.format("Part number %d already exists in the system", partNumber),
                    HttpStatus.CONFLICT);
        }
    }

    // ================================
    // VALIDATION / INVALID REQUEST
    // ================================

    public static class InvalidPrice extends PartException {
        public InvalidPrice() {
            super("INVALID_PRICE",
                    "Part price must be greater than or equal to 0",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static class InvalidQuantity extends PartException {
        public InvalidQuantity() {
            super("INVALID_QUANTITY",
                    "Part quantity must be greater than or equal to 0",
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static class InvalidStockAdjustment extends PartException {
        public InvalidStockAdjustment(String reason) {
            super("INVALID_STOCK_ADJUSTMENT",
                    String.format("Invalid stock adjustment: %s", reason),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static class NegativeQuantityResult extends PartException {
        public NegativeQuantityResult(int currentQuantity, int adjustment) {
            super("NEGATIVE_QUANTITY_RESULT",
                    String.format("Adjustment of %d would result in negative quantity (current: %d)",
                            adjustment, currentQuantity),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public static class InvalidAmount extends PartException {
        public InvalidAmount(String operation) {
            super("INVALID_AMOUNT",
                    String.format("Amount for %s operation must be positive", operation),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // ================================
    // INVENTORY / STOCK
    // ================================

    public static class InsufficientStock extends PartException {
        public InsufficientStock(Long partId, int available, int required) {
            super("INSUFFICIENT_STOCK",
                    String.format("Insufficient stock for part ID %d. Available: %d, Required: %d",
                            partId, available, required),
                    HttpStatus.CONFLICT);
        }
    }

    public static class PartOutOfStock extends PartException {
        public PartOutOfStock(Long partId, String partName) {
            super("PART_OUT_OF_STOCK",
                    String.format("Part '%s' (ID: %d) is out of stock", partName, partId),
                    HttpStatus.CONFLICT);
        }
    }

    public static class PartInactive extends PartException {
        public PartInactive(Long partId) {
            super("PART_INACTIVE",
                    String.format("Part with ID %d is inactive and cannot be used", partId),
                    HttpStatus.CONFLICT);
        }
    }

    public static class PartUnavailable extends PartException {
        public PartUnavailable(Long partId, String reason) {
            super("PART_UNAVAILABLE",
                    String.format("Part with ID %d is unavailable: %s", partId, reason),
                    HttpStatus.CONFLICT);
        }
    }

    // ================================
    // BUSINESS LOGIC
    // ================================

    public static class CannotDeletePart extends PartException {
        public CannotDeletePart(Long partId, String reason) {
            super("CANNOT_DELETE_PART",
                    String.format("Cannot delete part ID %d: %s", partId, reason),
                    HttpStatus.CONFLICT);
        }
    }

    public static class PartAlreadyInactive extends PartException {
        public PartAlreadyInactive(Long partId) {
            super("PART_ALREADY_INACTIVE",
                    String.format("Part with ID %d is already inactive", partId),
                    HttpStatus.CONFLICT);
        }
    }

    public static class PartAlreadyActive extends PartException {
        public PartAlreadyActive(Long partId) {
            super("PART_ALREADY_ACTIVE",
                    String.format("Part with ID %d is already active", partId),
                    HttpStatus.CONFLICT);
        }
    }
}

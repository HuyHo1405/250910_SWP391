package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class InvoiceException extends BaseServiceException {
    public InvoiceException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // ================================
    // GENERAL
    // ================================

    public static class NotFound extends InvoiceException {
        public NotFound() {
            super("INVOICE_NOT_FOUND", "Không tìm thấy hóa đơn", HttpStatus.NOT_FOUND);
        }
    }

    public static class BookingAlreadyHasInvoice extends InvoiceException {
        public BookingAlreadyHasInvoice() {
            super("BOOKING_HAS_INVOICE", "Booking này đã có hóa đơn được khởi tạo", HttpStatus.CONFLICT);
        }
    }

    // ================================
    // STATE & STATUS
    // ================================

    public static class InvalidFinalizeState extends InvoiceException {
        public InvalidFinalizeState() {
            super("INVALID_FINALIZE_STATE", "Chỉ có thể chốt hóa đơn từ trạng thái DRAFT", HttpStatus.CONFLICT);
        }
    }

    public static class InvoiceAlreadyPaid extends InvoiceException {
        public InvoiceAlreadyPaid() {
            super("INVOICE_ALREADY_PAID", "Hóa đơn này đã được thanh toán", HttpStatus.CONFLICT);
        }
    }

    // ================================
    // PAYMENT
    // ================================

    public static class PaymentFailed extends InvoiceException {
        public PaymentFailed() {
            super("PAYMENT_FAILED", "Quá trình thanh toán thất bại", HttpStatus.BAD_REQUEST);
        }
    }

    public static class PaymentAmountMismatch extends InvoiceException {
        public PaymentAmountMismatch() {
            super("PAYMENT_AMOUNT_MISMATCH", "Số tiền thanh toán không khớp với tổng tiền của hóa đơn", HttpStatus.BAD_REQUEST);
        }
    }
}
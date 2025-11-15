package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.InvoiceItemType;
import com.example.demo.model.modelEnum.PaymentMethod;
import com.example.demo.model.modelEnum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {
    @Data
    @Builder
    public static class PaymentURL {
        // URL đầy đủ của VNPAY
        private String paymentUrl;

        // Mã đơn hàng của bạn (để Frontend tiện theo dõi)
        private String orderCode;
    }

    @Data
    @Builder
    public static class PaymentStatusDetail {
        private PaymentStatus status; // (PENDING, SUCCESSFUL, FAILED)
        private String responseCode; // Mã của VNPAY (vd: "00")
        private String message; // Tin nhắn (vd: "Thanh toán thành công!")
    }

    /**
     * DTO đặc biệt dùng để trả về cho Server VNPAY khi xử lý IPN.
     * (Backend -> VNPAY Server)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VnpayIpn {
        private String RspCode; // "00", "01", "97"...
        private String Message; // "Confirm Success", "Order not found"...
    }

    @Data
    @Builder
    public static class Transaction{
        // ID của chính thanh toán này
        private Long id;

        // --- Thông tin từ Invoice ---

        // Số hóa đơn liên quan
        private String invoiceNumber;

        // Mã đơn hàng (do hệ thống mình tự tạo)
        private String orderCode;

        // --- Thông tin thanh toán ---

        // Số tiền đã thanh toán
        private BigDecimal amount;

        // Trạng thái (SUCCESSFUL, FAILED, PENDING)
        private PaymentStatus status;

        // Phương thức thanh toán (VNPAY, CASH)
        private PaymentMethod paymentMethod;

        // Thời điểm tạo giao dịch (lúc bắt đầu)
        private LocalDateTime createdAt;

        // Thời điểm thanh toán thành công (nếu có)
        private LocalDateTime paidAt;

        // --- Thông tin tham chiếu (nếu có) ---

        // Mã tham chiếu từ cổng thanh toán (vd: VNPAY)
        private String transactionRef;

        // Mã lỗi/phản hồi từ cổng thanh toán ('00' là thành công)
        private String responseCode;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RefundResult {
        private String orderCode;
        private boolean success;
        private String message;
    }
}

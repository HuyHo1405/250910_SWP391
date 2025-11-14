package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.InvoiceItemType;
import com.example.demo.model.modelEnum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

}

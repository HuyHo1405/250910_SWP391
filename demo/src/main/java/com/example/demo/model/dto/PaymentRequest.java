package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.PaymentMethod;
import com.example.demo.model.modelEnum.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

public class PaymentRequest {

    @Data
    public static class CreatePaymentLink {
        @NotNull(message = "Mã hóa đơn không được để trống")
        private Long invoiceId;
    }

    @Data
    public static class Create {
        @NotNull(message = "Mã hóa đơn không được để trống")
        private Long invoiceId;

        @NotNull(message = "Phương thức thanh toán không được để trống")
        private PaymentMethod paymentMethod;

        @NotNull(message = "Số tiền không được để trống")
        @Positive(message = "Số tiền phải lớn hơn 0")
        private Double amount;

        @Size(max = 255, message = "Mã giao dịch không được vượt quá 255 ký tự")
        private String transactionRef;

        private PaymentStatus status; // Optional, will default to PENDING

        private LocalDateTime paidAt; // Optional, will be set automatically when status is COMPLETED
    }

    @Data
    public static class Update {
        @Positive(message = "Số tiền phải lớn hơn 0")
        private Double amount;

        private PaymentMethod paymentMethod;

        private PaymentStatus status;

        @Size(max = 255, message = "Mã giao dịch không được vượt quá 255 ký tự")
        private String transactionRef;

        private LocalDateTime paidAt;
    }

    @Data
    public static class StatusUpdate {
        @NotNull(message = "Trạng thái không được để trống")
        private PaymentStatus status;

        private String transactionRef;
    }

    @Data
    public static class Process {
        @NotBlank(message = "Mã giao dịch không được để trống")
        @Size(max = 255, message = "Mã giao dịch không được vượt quá 255 ký tự")
        private String transactionRef;
    }
}

package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.PaymentMethod;
import com.example.demo.model.modelEnum.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

public class PaymentRequest {

    @Data
    public static class Create {
        @NotNull(message = "Invoice ID must not be null")
        private Long invoiceId;

        @NotNull(message = "Payment method must not be null")
        private PaymentMethod paymentMethod;

        @NotNull(message = "Amount must not be null")
        @Positive(message = "Amount must be greater than 0")
        private Double amount;

        @Size(max = 255, message = "Transaction reference must not exceed 255 characters")
        private String transactionRef;

        private PaymentStatus status; // Optional, will default to PENDING

        private LocalDateTime paidAt; // Optional, will be set automatically when status is COMPLETED
    }

    @Data
    public static class Update {
        @Positive(message = "Amount must be greater than 0")
        private Double amount;

        private PaymentMethod paymentMethod;

        private PaymentStatus status;

        @Size(max = 255, message = "Transaction reference must not exceed 255 characters")
        private String transactionRef;

        private LocalDateTime paidAt;
    }

    @Data
    public static class StatusUpdate {
        @NotNull(message = "Status must not be null")
        private PaymentStatus status;

        private String transactionRef;
    }

    @Data
    public static class Process {
        @NotBlank(message = "Transaction reference must not be blank")
        @Size(max = 255, message = "Transaction reference must not exceed 255 characters")
        private String transactionRef;
    }
}

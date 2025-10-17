package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.InvoiceStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

public class InvoiceRequest {

    @Data
    public static class Create {
        @NotNull(message = "Booking ID must not be null")
        private Long bookingId;

        @Size(max = 50, message = "Invoice number must not exceed 50 characters")
        private String invoiceNumber; // Optional, will auto-generate if not provided

        @NotNull(message = "Issue date must not be null")
        private LocalDateTime issueDate;

        private LocalDateTime dueDate; // Optional, will default to 30 days from issue date

        @NotNull(message = "Total amount must not be null")
        @PositiveOrZero(message = "Total amount must be >= 0")
        private Double totalAmount;

        @NotNull(message = "Tax amount must not be null")
        @PositiveOrZero(message = "Tax amount must be >= 0")
        private Double taxAmount;

        @PositiveOrZero(message = "Discount amount must be >= 0")
        private Double discountAmount;

        private InvoiceStatus status; // Optional, will default to DRAFT
    }

    @Data
    public static class Update {
        @PositiveOrZero(message = "Total amount must be >= 0")
        private Double totalAmount;

        @PositiveOrZero(message = "Tax amount must be >= 0")
        private Double taxAmount;

        @PositiveOrZero(message = "Discount amount must be >= 0")
        private Double discountAmount;

        private LocalDateTime dueDate;

        private InvoiceStatus status;
    }

    @Data
    public static class StatusUpdate {
        @NotNull(message = "Status must not be null")
        private InvoiceStatus status;
    }
}

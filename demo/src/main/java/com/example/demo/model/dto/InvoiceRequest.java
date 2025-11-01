package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.InvoiceStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

public class InvoiceRequest {

    @Data
    public static class Create {
        @NotNull(message = "Mã đặt lịch không được để trống")
        private Long bookingId;

        @Size(max = 50, message = "Số hóa đơn không được vượt quá 50 ký tự")
        private String invoiceNumber; // Optional, will auto-generate if not provided

        @NotNull(message = "Ngày xuất hóa đơn không được để trống")
        private LocalDateTime issueDate;

        private LocalDateTime dueDate; // Optional, will default to 30 days from issue date

        private InvoiceStatus status; // Optional, will default to DRAFT
    }

    @Data
    @AllArgsConstructor
    public static class Update {

        private LocalDateTime dueDate;

        private InvoiceStatus status;
    }

    @Data
    public static class StatusUpdate {
        @NotNull(message = "Trạng thái không được để trống")
        private InvoiceStatus status;
    }
}

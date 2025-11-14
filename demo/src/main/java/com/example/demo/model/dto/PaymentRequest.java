package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.InvoiceItemType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentRequest {
    @Data
    public static class CreatePayment {
        @NotNull(message = "ID của hóa đơn không được để trống")
        private Long invoiceId; // ID của hóa đơn (Invoice) cần thanh toán
    }

}

package com.example.demo.model.dto;

import com.example.demo.model.entity.Payment;
import com.example.demo.model.modelEnum.PaymentMethod;
import com.example.demo.model.modelEnum.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    // ================================
    // BASIC INFO
    // ================================
    private Long id;
    private PaymentMethod paymentMethod;
    private Double amount;
    private PaymentStatus status;
    private String transactionRef;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ================================
    // RELATIONAL INFO
    // ================================
    private Long invoiceId;
    private String invoiceNumber;

    // ================================
    // MAPPER
    // ================================
    public static PaymentResponse fromEntity(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentResponse.PaymentResponseBuilder builder = PaymentResponse.builder()
                .id(payment.getId())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionRef(payment.getTransactionRef())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt());

        // Add invoice info if available
        if (payment.getInvoice() != null) {
            builder.invoiceId(payment.getInvoice().getId())
                   .invoiceNumber(payment.getInvoice().getInvoiceNumber());
        }

        return builder.build();
    }
}

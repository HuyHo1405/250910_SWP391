package com.example.demo.model.dto;

import com.example.demo.model.entity.Invoice;
import com.example.demo.model.modelEnum.InvoiceStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {

    // ================================
    // BASIC INFO
    // ================================
    private Long id;
    private String invoiceNumber;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private Double totalAmount;
    private Double taxAmount;
    private Double discountAmount;
    private Double finalAmount;
    private InvoiceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ================================
    // RELATIONAL INFO
    // ================================
    private Long bookingId;
    private String bookingReference;
    private Long customerId;
    private String customerName;
    private String customerEmail;

    // Payment summary
    private Integer totalPayments;
    private Double totalPaidAmount;
    private Double remainingAmount;
    private Boolean isFullyPaid;

    // ================================
    // DETAILED INFO (Optional)
    // ================================
    private List<PaymentResponse> payments;

    // ================================
    // MAPPER
    // ================================
    public static InvoiceResponse fromEntity(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        InvoiceResponse.InvoiceResponseBuilder builder = InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .totalAmount(invoice.getTotalAmount())
                .taxAmount(invoice.getTaxAmount())
                .discountAmount(invoice.getDiscountAmount())
                .finalAmount(invoice.getFinalAmount())
                .status(invoice.getStatus())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt());

        // Add booking info if available
        if (invoice.getBooking() != null) {
            builder.bookingId(invoice.getBooking().getId());
            // Add booking reference if available
            // builder.bookingReference(invoice.getBooking().getReference());

            // Add customer info if available through booking
            if (invoice.getBooking().getCustomer() != null) {
                builder.customerId(invoice.getBooking().getCustomer().getId())
                       .customerName(invoice.getBooking().getCustomer().getFullName())
                       .customerEmail(invoice.getBooking().getCustomer().getEmailAddress());
            }
        }

        // Calculate payment summary
        if (invoice.getPayments() != null) {
            double totalPaid = invoice.getPayments().stream()
                    .filter(payment -> payment.getStatus().toString().equals("COMPLETED"))
                    .mapToDouble(payment -> payment.getAmount() != null ? payment.getAmount() : 0.0)
                    .sum();

            builder.totalPayments(invoice.getPayments().size())
                   .totalPaidAmount(totalPaid)
                   .remainingAmount(invoice.getFinalAmount() - totalPaid)
                   .isFullyPaid(Math.abs(invoice.getFinalAmount() - totalPaid) < 0.01);
        } else {
            builder.totalPayments(0)
                   .totalPaidAmount(0.0)
                   .remainingAmount(invoice.getFinalAmount())
                   .isFullyPaid(false);
        }

        return builder.build();
    }

    // Mapper with payment details
    public static InvoiceResponse fromEntityWithPayments(Invoice invoice) {
        InvoiceResponse response = fromEntity(invoice);
        if (invoice != null && invoice.getPayments() != null) {
            List<PaymentResponse> paymentResponses = invoice.getPayments().stream()
                    .map(PaymentResponse::fromEntity)
                    .toList();
            response.setPayments(paymentResponses);
        }
        return response;
    }

    // Mapper for summary view (without payment details)
    public static InvoiceResponse fromEntitySummary(Invoice invoice) {
        return fromEntity(invoice); // Same as basic mapper
    }
}

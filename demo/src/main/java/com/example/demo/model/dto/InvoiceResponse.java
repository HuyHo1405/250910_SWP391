package com.example.demo.model.dto;

import com.example.demo.model.entity.Invoice;
import com.example.demo.model.modelEnum.InvoiceStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private List<InvoiceLineResponse> invoiceLines;
}

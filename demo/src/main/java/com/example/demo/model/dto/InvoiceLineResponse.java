package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.InvoiceItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLineResponse {
    private long id;
    private String itemDescription;
    private InvoiceItemType itemType; // Enum: InvoiceItemType (SERVICE hoặc PART)
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}

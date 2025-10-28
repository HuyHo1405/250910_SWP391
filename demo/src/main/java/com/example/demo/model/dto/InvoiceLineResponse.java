package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.InvoiceItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLineResponse {
    private long id;
    private String itemDescription;
    private InvoiceItemType itemType; // Enum: InvoiceItemType (SERVICE hoặc PART)
    private Double quantity;
    private Double unitPrice;
    private Double taxRate;
}

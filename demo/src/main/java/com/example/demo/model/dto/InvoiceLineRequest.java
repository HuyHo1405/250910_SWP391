package com.example.demo.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InvoiceLineRequest {
    @NotBlank(message = "Mô tả không được để trống")
    private String itemDescription;
    @NotBlank(message = "Loại mục phải khai báo")
    private String itemType;
    @NotNull
    @Positive
    private Double quantity;
    @NotNull @PositiveOrZero
    private Double unitPrice;
    @NotNull @Min(0) @Max(1)
    private Double taxRate;
}

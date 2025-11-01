package com.example.demo.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InvoiceLineRequest {
    @NotBlank(message = "Mô tả không được để trống")
    private String itemDescription;
    @NotBlank(message = "Loại mục phải khai báo")
    private String itemType;
    @NotNull(message = "Số lượng phải khai báo")
    @Positive(message = "Số lượng phải không âm")
    private Double quantity;
    @NotNull @PositiveOrZero
    private Double unitPrice;
}

package com.example.demo.model.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogModelPartRequest {

    @NotNull(message = "Mã phụ tùng không được để trống")
    private Long partId;

    @Positive(message = "Số lượng phải lớn hơn 0")
    private BigDecimal quantityRequired;

    private Boolean isOptional = false;

    @Size(max = 500, message = "Ghi chú phụ tùng không được vượt quá 500 ký tự")
    private String notes;
}

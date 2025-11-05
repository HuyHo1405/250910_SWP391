package com.example.demo.model.dto;

import jakarta.validation.Valid;
import lombok.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogModelRequest {

    @NotNull(message = "mã mẫu xe không được để trống")
    private Long modelId;

    @Positive(message = "Thời gian ước tính phải là số dương")
    private Double estTimeMinutes;

    @PositiveOrZero(message = "Giá bảo dưỡng phải là số không âm")
    private BigDecimal maintenancePrice;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String notes;

    @Valid
    private List<CatalogModelPartRequest> parts;

}

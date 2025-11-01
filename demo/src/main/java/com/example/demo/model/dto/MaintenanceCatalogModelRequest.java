package com.example.demo.model.dto;

import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceCatalogModelRequest {

    @NotNull(message = "mã mẫu xe không được để trống")
    private Long modelId;

    @Positive(message = "Thời gian ước tính phải là số dương")
    private Double estTimeMinutes;

    @PositiveOrZero(message = "Giá bảo dưỡng phải là số không âm")
    private Double maintenancePrice;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String notes;

}

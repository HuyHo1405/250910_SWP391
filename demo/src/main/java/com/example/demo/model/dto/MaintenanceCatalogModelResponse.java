package com.example.demo.model.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceCatalogModelResponse {
    private Long modelId;
    private String modelName;
    private String modelBrand;
    private String modelYear;
    private Double estTimeMinutes;
    private Double maintenancePrice;
    private String notes;
    private LocalDateTime createdAt;

    private List<MaintenanceCatalogModelPartResponse> parts;
}

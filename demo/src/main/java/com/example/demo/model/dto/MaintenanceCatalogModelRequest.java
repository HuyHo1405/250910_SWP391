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

    @NotNull(message = "Catalog ID is required")
    private Long maintenanceCatalogId;

    @NotNull(message = "Model ID is required")
    private Long modelId;

    @Positive(message = "Estimated time must be positive")
    private Double estTimeMinutes;

    @PositiveOrZero(message = "Maintenance price must be non-negative")
    private Double maintenancePrice;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @Valid
    private List<MaintenanceCatalogModelPartRequest> parts;
}

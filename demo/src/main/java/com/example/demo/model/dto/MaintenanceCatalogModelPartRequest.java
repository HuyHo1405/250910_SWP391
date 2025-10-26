package com.example.demo.model.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceCatalogModelPartRequest {

    @NotNull(message = "Part ID is required")
    private Long partId;

    @Positive(message = "Quantity must be positive")
    private Integer quantityRequired;

    private Boolean isOptional = false;

    @Size(max = 500, message = "Part notes cannot exceed 500 characters")
    private String notes;
}

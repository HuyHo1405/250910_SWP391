package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MaintenanceServiceRequest {
    @NotBlank(message = "Service name must not be blank")
    private String name;

    private String description;

    @NotNull(message = "Estimated time is required")
    @Positive(message = "Estimated time must be greater than 0")
    private Double estTimeHours;

    @NotNull(message = "Service price is required")
    @Positive(message = "Service price must be greater than 0")
    private Double currentPrice;
}

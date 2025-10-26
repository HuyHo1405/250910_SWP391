package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.MaintenanceCatalogType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MaintenanceCatalogRequest {

    @NotBlank(message = "Service name must not be blank")
    private String name;

    private String description;

    @NotNull(message = "Service type is required")
    private MaintenanceCatalogType maintenanceServiceType;

}

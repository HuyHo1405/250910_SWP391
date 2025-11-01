package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.MaintenanceCatalogType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MaintenanceCatalogRequest {

    @NotBlank(message = "Tên dịch vụ không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Loại dịch vụ không được để trống")
    private MaintenanceCatalogType maintenanceServiceType;

}

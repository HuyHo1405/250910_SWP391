package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.MaintenanceCatalogType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CatalogRequest {

    @NotBlank(message = "Tên dịch vụ không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Loại dịch vụ không được để trống")
    private MaintenanceCatalogType maintenanceServiceType;

    @Valid // Thêm @Valid để kiểm tra các DTO lồng bên trong
    @NotNull(message = "Danh sách model không được để trống")
    @Size(min = 1, message = "Phải có ít nhất 1 model áp dụng")
    private List<CatalogModelRequest> models;

}

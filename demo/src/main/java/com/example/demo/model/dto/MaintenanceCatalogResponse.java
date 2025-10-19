package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.modelEnum.MaintenanceCatalogType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceCatalogResponse {

    private Long id;
    private String name;
    private String description;
    private MaintenanceCatalogType maintenanceServiceType;
    private Double estTimeMinutes;
    private Double currentPrice;
    private EntityStatus status;
    private LocalDateTime createdAt;

    private List<MaintenanceCatalogModelResponse> models;
}

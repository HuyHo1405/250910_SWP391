package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.modelEnum.MaintenanceCatalogCategory;
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
public class CatalogResponse {

    private Long id;
    private String name;
    private String description;
    private String maintenanceServiceCategory;
    private EntityStatus status;
    private LocalDateTime createdAt;

    private List<CatalogModelResponse> models;
}

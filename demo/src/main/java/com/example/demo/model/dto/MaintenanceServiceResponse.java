package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.MaintenanceServiceType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MaintenanceServiceResponse {
    private Long id;
    private String name;
    private String description;
    private MaintenanceServiceType maintenanceServiceType; // New field
    private Double estTimeMinutes;
    private Double currentPrice;
    private String status;
    private LocalDateTime createdAt;
}

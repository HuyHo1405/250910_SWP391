package com.example.demo.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MaintenanceServiceResponse {
    private Long id;
    private String name;
    private String description;
    private Double estTimeHours;
    private Double currentPrice;
    private String status;
    private LocalDateTime createdAt;
}

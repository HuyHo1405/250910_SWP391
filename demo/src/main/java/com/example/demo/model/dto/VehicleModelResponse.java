package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleModelResponse {
    private Long id;
    private String brandName;
    private String modelName;
    private String dimensions;
    private String yearIntroduce;
    private Integer seats;
    private Double batteryCapacityKwh;
    private Double rangeKm;
    private Double chargingTimeHours;
    private Double motorPowerKw;
    private Double weightKg;
    private EntityStatus status;
    private LocalDateTime createdAt;
}

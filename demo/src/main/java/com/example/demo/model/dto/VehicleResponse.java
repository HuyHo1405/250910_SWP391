package com.example.demo.model.dto;

import com.example.demo.model.entity.Vehicle;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {

    // ================================
    // BASIC INFO
    // ================================
    private String vin;
    private String name;
    private String plateNumber;
    private String color;
    private Double distanceTraveledKm;
    private Double batteryDegradation;
    private LocalDateTime purchasedAt;
    private LocalDateTime createdAt;
    private String entityStatus;

    // ================================
    // RELATIONAL INFO
    // ================================
    private Long userId;
    private String username;
    private Long modelId;
    private String modelName;

    // ================================
    // MAPPER
    // ================================
    public static VehicleResponse fromEntity(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return VehicleResponse.builder()
                .vin(vehicle.getVin())
                .name(vehicle.getName())
                .plateNumber(vehicle.getPlateNumber())
                .color(vehicle.getColor())
                .distanceTraveledKm(vehicle.getDistanceTraveledKm())
                .batteryDegradation(vehicle.getBatteryDegration())
                .purchasedAt(vehicle.getPurchasedAt())
                .createdAt(vehicle.getCreatedAt())
                .entityStatus(vehicle.getEntityStatus().name())

                // Quan hệ user
                .userId(vehicle.getCustomer() != null ? vehicle.getCustomer().getId() : null)
                .username(vehicle.getCustomer() != null ? vehicle.getCustomer().getFullName() : null)

                // Quan hệ model
                .modelId(vehicle.getModel() != null ? vehicle.getModel().getId() : null)
                .modelName(vehicle.getModel() != null ? vehicle.getModel().getModelName() : null)

                .build();
    }
}

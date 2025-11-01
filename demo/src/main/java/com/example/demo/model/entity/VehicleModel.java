package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.EntityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_models")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VehicleModel {

    // ================================
    // COLUMNS - Database Fields
    // ================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String brandName;

    @Column(name = "model_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String modelName;

    @Column(name = "dimensions", columnDefinition = "NVARCHAR(255)")
    private String dimensions;

    @Column(name = "seats", nullable = false)
    private Integer seats;

    @Column(name = "battery_capacity_kwh")
    private Double batteryCapacityKwh;

    @Column(name = "range_km")
    private Double rangeKm;

    @Column(name = "charging_time_hours")
    private Double chargingTimeHours;

    @Column(name = "motor_power_kw")
    private Double motorPowerKw;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EntityStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ================================
    // PRE/POST FUNCTIONS - Lifecycle Callbacks
    // ================================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

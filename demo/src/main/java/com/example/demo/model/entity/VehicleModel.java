package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column
    private String dimensions;

    @Column(name = "year_introduce", length = 4)
    private String yearIntroduce;

    @Column
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

    // status là enum EntityStatus
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

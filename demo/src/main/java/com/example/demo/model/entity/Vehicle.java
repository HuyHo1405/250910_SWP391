package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.EntityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Vehicle {

    // ================================
    // COLUMNS - Database Fields
    // ================================

    @Id
    @Column(name = "vin", nullable = false, length = 255) // vin là PK
    private String vin;

    @Column(name = "name")
    private String name;

    @Column(name = "plate_number", unique = true)
    private String plateNumber;

    @Column(name = "color", columnDefinition = "NVARCHAR(12)")
    private String color;

    @Column(name = "distance_traveled_km")
    private Double distanceTraveledKm;

    @Column(name = "battery_degradation")
    private Double batteryDegradation;

    @Column(name = "purchased_at", nullable = false)
    private LocalDateTime purchasedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_status", nullable = false)
    private EntityStatus entityStatus = EntityStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // ManyToOne với Model
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_model_id", nullable = false)
    private VehicleModel model;
}

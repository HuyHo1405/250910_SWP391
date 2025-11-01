package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_catalogs_models")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceCatalogModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_catalog_id", nullable = false)
    private MaintenanceCatalog maintenanceCatalog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private VehicleModel vehicleModel;

    @Column(name = "est_time_minutes")
    private Double estTimeMinutes;

    @Column(name = "maintenance_price")
    private Double maintenancePrice;

    @Column(name = "notes", columnDefinition = "NVARCHAR(255)")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}

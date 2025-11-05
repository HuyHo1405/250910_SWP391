package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.EntityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private BigDecimal maintenancePrice;

    @Column(name = "notes", columnDefinition = "NVARCHAR(255)")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EntityStatus status;

    @OneToMany(mappedBy = "maintenanceCatalogModel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaintenanceCatalogModelPart> parts = new ArrayList<>();


    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public void addPart(MaintenanceCatalogModelPart part) {
        this.parts.add(part);
        part.setMaintenanceCatalogModel(this);
    }

    public void removePart(MaintenanceCatalogModelPart part) {
        this.parts.remove(part);
        part.setMaintenanceCatalogModel(null);
    }
}

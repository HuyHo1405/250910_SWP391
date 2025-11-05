package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "maintenance_catalogs_models_parts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceCatalogModelPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_catalog_model_id", nullable = false)
    private MaintenanceCatalogModel maintenanceCatalogModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(name = "quantity_required")
    private BigDecimal quantityRequired;

    @Column(name = "is_optional")
    private Boolean isOptional;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String notes;
}

package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "services_models_parts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ServiceModelPart {

    // ================================
    // COLUMNS - Database Fields
    // ================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(name = "model_id", nullable = false)
    private Integer modelId;

    @Column(name = "part_id", nullable = false)
    private Integer partId;

    @Column(name = "quantity_required", nullable = false)
    private Integer quantityRequired;

    @Column(name = "is_optional", nullable = false)
    private Boolean isOptional;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ================================
    // RELATIONSHIPS - Entity Relationships
    // ================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private ServiceModel service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", insertable = false, updatable = false)
    private VehicleModel vehicleModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", insertable = false, updatable = false)
    private Part part;
}

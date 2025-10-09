package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parts_work_with_model")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PartWorkWithModel {

    // ================================
    // COLUMNS - Database Fields
    // ================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "part_id", nullable = false)
    private Long partId;

    @Column(name = "model_id", nullable = false)
    private Long modelId;

    @Column(name = "part_number", nullable = false)
    private Integer partNumber;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "quantity_per_model", nullable = false)
    private Integer quantityPerModel;

    // ================================
    // RELATIONSHIPS - JPA Relationships
    // ================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", insertable = false, updatable = false)
    private Part part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", insertable = false, updatable = false)
    private VehicleModel vehicleModel;
}

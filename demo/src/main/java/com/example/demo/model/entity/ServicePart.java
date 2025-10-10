package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_parts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ServicePart {

    // ================================
    // COLUMNS - Database Fields
    // ================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ================================
    // PRE/POST FUNCTIONS - Lifecycle Callbacks
    // ================================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ================================
    // HELPER FUNCTIONS - Business Logic
    // ================================

    public ServicePart(Service service, Part part) {
        this.service = service;
        this.part = part;
    }
}

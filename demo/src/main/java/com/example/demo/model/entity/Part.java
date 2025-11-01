package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.EntityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "parts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Part {

    // ================================
    // COLUMNS - Database Fields
    // ================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(name = "part_number", nullable = false)
    private String partNumber;

    @Column(name = "manufacturer", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String manufacturer;

    @Column(name = "description", columnDefinition = "NVARCHAR(255)")
    private String description;

    @Column(name = "current_unit_price", nullable = false)
    private Double currentUnitPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "reserved", nullable = false)
    private Integer reserved;

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

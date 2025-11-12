package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.modelEnum.PartCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private PartCategory category;

    @Column(name = "current_unit_price", nullable = false)
    private BigDecimal currentUnitPrice;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "reserved", nullable = false)
    private BigDecimal reserved;

    @Column(name = "used", nullable = false)
    private BigDecimal used;

    @Column(name = "image_url", columnDefinition = "NVARCHAR(500)")
    private String imageUrl;

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

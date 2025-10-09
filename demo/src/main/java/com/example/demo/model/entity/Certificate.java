package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Certificate {

    // ================================
    // COLUMNS - Database Fields
    // ================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "issuer", nullable = false, length = 255)
    private String issuer;

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

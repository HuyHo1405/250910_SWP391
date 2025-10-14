
package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.modelEnum.MaintenanceCatalogType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private MaintenanceCatalogType maintenanceServiceType;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "current_price")
    private Double currentPrice;

    @Column(name = "est_time_minutes")
    private Double estTimeMinutes;

    @Enumerated(EnumType.STRING)
    private EntityStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) status = EntityStatus.ACTIVE;
    }


}

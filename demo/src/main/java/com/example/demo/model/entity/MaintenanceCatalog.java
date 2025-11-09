
package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.modelEnum.MaintenanceCatalogCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maintenance_catalogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceCatalogCategory maintenanceServiceCategory;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityStatus status;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "maintenanceCatalog", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaintenanceCatalogModel> models = new ArrayList<>();

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) status = EntityStatus.ACTIVE;
    }

    public void addModel(MaintenanceCatalogModel model) {
        this.models.add(model);
        model.setMaintenanceCatalog(this);
    }

    public void removeModel(MaintenanceCatalogModel model) {
        this.models.remove(model);
        model.setMaintenanceCatalog(null);
    }

}
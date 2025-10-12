package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.EntityStatus;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {

    // ================================
    // COLUMNS - Database Fields
    // ================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "email_address", nullable = false, unique = true, length = 255)
    private String emailAddress;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "hashed_password", nullable = false, length = 255)
    private String hashedPassword;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EntityStatus status;

    @Column(name = "login_at")
    private LocalDateTime loginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Set<Vehicle> vehicles;

    // ================================
    // PRE/POST FUNCTIONS - Lifecycle Callbacks
    // ================================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    @PostConstruct
    private void initCollections() {
        if (vehicles == null) {
            vehicles = new HashSet<>();
        }
    }

    // ================================
    // HELPER FUNCTIONS - Business Logic
    // ================================

//    public void addVehicle(Vehicle v) {
//        vehicles.add(v);
//        v.setCustomer(this);
//    }
//
//    public void removeVehicle(Vehicle v) {
//        vehicles.remove(v);
//        v.setCustomer(null);
//    }
}
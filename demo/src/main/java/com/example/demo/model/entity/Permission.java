package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "resource", columnDefinition = "NVARCHAR(255)")
    private String resource;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "description", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Quan hệ nhiều-nhiều với Role
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    // Helper methods
    public void addRole(Role role) {
        this.roles.add(role);
        role.getPermissions().add(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getPermissions().remove(this);
    }
}

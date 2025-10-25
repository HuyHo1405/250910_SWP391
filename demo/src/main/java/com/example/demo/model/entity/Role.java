package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true, name = "display_name")
    private String displayName;

    @Column(length = 500)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "role_editable",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "editable_id")
    )
    private Set<Role> editableRoles = new HashSet<>();

    //helper methods
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
        permission.getRoles().add(this);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
        permission.getRoles().remove(this);
    }
}

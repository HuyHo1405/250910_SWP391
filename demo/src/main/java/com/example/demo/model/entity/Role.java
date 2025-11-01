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

    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(nullable = false, unique = true, name = "display_name", columnDefinition = "NVARCHAR(255)")
    private String displayName;

    @Column(length = 500, columnDefinition = "NVARCHAR(255)")
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
}

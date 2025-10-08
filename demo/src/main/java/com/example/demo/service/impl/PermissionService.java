package com.example.demo.service.impl;

import com.example.demo.exception.UserException;
import com.example.demo.model.entity.Permission;
import com.example.demo.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    /**
     * Check if user has specific permission
     * Uses caching for performance
     */
    @Cacheable(value = "permissions", key = "#user.id + ':' + #resource + ':' + #action")
    public boolean hasPermission(User user, String resource, String action) {
        if (user.getRole() == null) {
            throw new UserException.RoleNotFound();
        }

        boolean hasPermission = user.getRole().getPermissions().stream()
                .anyMatch(permission ->
                        permission.getResource().equalsIgnoreCase(resource) &&
                                permission.getAction().equalsIgnoreCase(action) &&
                                Boolean.TRUE.equals(permission.getIsActive())
                );

        log.debug("Permission check: user={}, resource={}, action={}, result={}",
                user.getId(), resource, action, hasPermission);

        return hasPermission;
    }

    /**
     * Check permission and throw exception if denied
     */
    public void checkPermission(User user, String resource, String action) {
        if (!hasPermission(user, resource, action)) {
            log.warn("Access denied: user {} lacks {} permission on {}",
                    user.getId(), action, resource);
            throw new AccessDeniedException(
                    "Access denied. Required permission: " + action + " on " + resource
            );
        }
    }

    /**
     * Check if user has ANY of the specified permissions
     */
    public boolean hasAnyPermission(User user, String resource, String... actions) {
        for (String action : actions) {
            if (hasPermission(user, resource, action)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user has ALL of the specified permissions
     */
    public boolean hasAllPermissions(User user, String resource, String... actions) {
        for (String action : actions) {
            if (!hasPermission(user, resource, action)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get all permissions for a user on a resource
     */
    @Cacheable(value = "userPermissions", key = "#user.id + ':' + #resource")
    public Set<String> getUserPermissions(User user, String resource) {
        if (user.getRole() == null) {
            throw new UserException.RoleNotFound();
        }

        return user.getRole().getPermissions().stream()
                .filter(p -> p.getResource().equalsIgnoreCase(resource))
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .map(Permission::getAction)
                .collect(Collectors.toSet());
    }
}

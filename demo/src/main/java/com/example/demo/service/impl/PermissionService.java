package com.example.demo.service.impl;

import com.example.demo.exception.UserException;
import com.example.demo.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {
    public boolean hasPermission(User user, String resource, String action) {
        if (user.getRole() == null) {
            throw new UserException.RoleNotFound();
        }

        return user.getRole().getPermissions().stream()
                .anyMatch(permission ->
                        permission.getResource().equalsIgnoreCase(resource) &&
                                permission.getAction().equalsIgnoreCase(action) &&
                                Boolean.TRUE.equals(permission.getIsActive())
                );
    }

    public void checkPermission(User user, String resource, String action) {
        if (!hasPermission(user, resource, action)) {
            throw new AccessDeniedException(
                    "Access denied. Required permission: " + action + " on " + resource
            );
        }
    }

    public boolean hasAnyPermission(User user, String resource, String... actions) {
        for (String action : actions) {
            if (hasPermission(user, resource, action)) return true;
        }
        return false;
    }

    public boolean hasAllPermissions(User user, String resource, String... actions) {
        for (String action : actions) {
            if (!hasPermission(user, resource, action)) return false;
        }
        return true;
    }
}

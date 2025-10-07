package com.example.demo.service.impl;

import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.VehicleException;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Centralized access control service
 * Logic: ADMIN/STAFF have full access, CUSTOMER only access their own resources
 */
@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final CurrentUserService currentUserService;
    private final PermissionService permissionService;

    public void verifyVehicleAccess(Vehicle vehicle, String action) {
        verifyVehicleAccess(vehicle.getUser().getId(), action);
    }

    public void verifyVehicleAccess(Long ownerId, String action) {
        User currentUser = currentUserService.getCurrentUser();

        // Admin/Staff have full access - check by role first (fast path)
        if (currentUserService.isStaffOrAdmin()) {
            return;
        }

        // Technician can only READ
        if (currentUserService.isTechnician()) {
            if (!"read".equalsIgnoreCase(action)) {
                throw new VehicleException.UnauthorizedAccess();
            }
            return;
        }

        // Customer can only access their own vehicles
        if (currentUserService.isCustomer()) {
            if (!currentUser.getId().equals(ownerId)) {
                throw new VehicleException.UnauthorizedAccess();
            }

            // Additionally verify customer has the required permission
            permissionService.checkPermission(currentUser, "VEHICLE", action);
            return;
        }

        // Unknown role - deny by default
        throw new VehicleException.UnauthorizedAccess();
    }

    public void verifyAdminOrStaffAccess() {
        if (!currentUserService.isStaffOrAdmin()) {
            throw new VehicleException.UnauthorizedAccess();
        }
    }

    public void verifyResourceAccess(Long resourceOwnerId, String resourceType, String action) {
        User currentUser = currentUserService.getCurrentUser();

        // Admin/Staff have full access
        if (currentUserService.isStaffOrAdmin()) {
            return;
        }

        // Technician can only READ
        if (currentUserService.isTechnician()) {
            if (!"read".equalsIgnoreCase(action)) {
                throw new VehicleException.UnauthorizedAccess();
            }
            return;
        }

        // Customer can only access their own resources
        if (currentUserService.isCustomer()) {
            if (!currentUser.getId().equals(resourceOwnerId)) {
                throw new AccessDeniedException("Access denied. You can only " + action + " your own " + resourceType);
            }

            // Verify permission
            permissionService.checkPermission(currentUser, resourceType, action);
            return;
        }

        throw new AccessDeniedException("Access denied for resource: " + resourceType);
    }

    public boolean isResourceOwner(Long resourceOwnerId) {
        return currentUserService.getCurrentUserId().equals(resourceOwnerId);
    }
}
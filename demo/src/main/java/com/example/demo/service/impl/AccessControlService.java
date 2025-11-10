package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.exception.UserException;
import com.example.demo.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessControlService {

    private final CurrentUserService currentUserService;
    private final PermissionService permissionService;

    public void verifyResourceAccess(Long resourceOwnerId, String resourceType, String action) {
        User currentUser = currentUserService.getCurrentUser();

        log.debug("Checking access: user={}, resource={}, action={}, owner={}",
                currentUser.getId(), resourceType, action, resourceOwnerId);

        // 1. Check if user has the required permission in database
        if (!permissionService.hasPermission(currentUser, resourceType, action)) {
            log.warn("Permission denied: user {} lacks {} permission on {}",
                    currentUser.getId(), action, resourceType);
            // ✅ Sửa: Dùng UserException thay vì VehicleException
            throw new UserException.InsufficientPermissions();
        }

        // 2. Check ownership if required (for non-admin/staff users)
        if (requiresOwnershipCheck(currentUser, resourceType) && resourceOwnerId != null) {
            if (!currentUser.getId().equals(resourceOwnerId)) {
                log.warn("Ownership check failed: user {} tried to {} resource owned by {}",
                        currentUser.getId(), action, resourceOwnerId);
                // ✅ Sửa: Dùng CommonException.Forbidden thay vì VehicleException
                throw new CommonException.Forbidden(
                        "Bạn không được phép truy cập tài nguyên này"
                );
            }
        }

        log.debug("Access granted: user {} can {} on {} (owner={})",
                currentUser.getId(), action, resourceType, resourceOwnerId);
    }

    public void verifyResourceAccessWithoutOwnership(String resourceType, String action) {
        User currentUser = currentUserService.getCurrentUser();

        log.debug("Checking access (no ownership): user={}, resource={}, action={}",
                currentUser.getId(), resourceType, action);

        // Only check permission, no ownership validation
        if (!permissionService.hasPermission(currentUser, resourceType, action)) {
            log.warn("Permission denied: user {} lacks {} permission on {}",
                    currentUser.getId(), action, resourceType);
            // ✅ Sửa: Dùng UserException thay vì VehicleException
            throw new UserException.InsufficientPermissions();
        }

        log.debug("Access granted: user {} can {} on {}",
                currentUser.getId(), action, resourceType);
    }

    public void verifyCanAccessAllResources(String resourceType, String action) {
        User currentUser = currentUserService.getCurrentUser();

        log.debug("Checking access to ALL {}: user={}, action={}",
                resourceType, currentUser.getId(), action);

        // 1. Must have bypass_ownership permission
        if (!permissionService.hasPermission(currentUser, "SYSTEM", "bypass_ownership")) {
            log.warn("User {} lacks bypass_ownership, cannot access all {}",
                    currentUser.getId(), resourceType);
            // ✅ Sửa: Dùng UserException thay vì VehicleException
            throw new UserException.InsufficientPermissions();
        }

        // 2. Must have the required permission on the resource
        if (!permissionService.hasPermission(currentUser, resourceType, action)) {
            log.warn("User {} lacks {} permission on {}",
                    currentUser.getId(), action, resourceType);
            // ✅ Sửa: Dùng UserException thay vì VehicleException
            throw new UserException.InsufficientPermissions();
        }

        log.debug("Access granted: user {} can access ALL {} with {}",
                currentUser.getId(), resourceType, action);
    }

    public boolean isResourceOwner(Long resourceOwnerId) {
        return currentUserService.getCurrentUserId().equals(resourceOwnerId);
    }

    private boolean requiresOwnershipCheck(User user, String resourceType) {
        // Check if user has "bypass_ownership" privilege
        // This can be a special permission in database
        if (permissionService.hasPermission(user, "SYSTEM", "bypass_ownership")) {
            return false;
        }

        return !permissionService.hasPermission(user, resourceType, "bypass_ownership");
    }
}

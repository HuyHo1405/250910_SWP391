package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.entity.Role;
import com.example.demo.repo.RoleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserContextService {

    private final CurrentUserService currentUserService;
    private final RoleRepo roleRepo;

    public boolean isAdmin() {
        boolean result = hasRole("ADMIN");
        log.debug("Checking if user is ADMIN: {}", result);
        return result;
    }

    public boolean isStaff() {
        boolean result = hasRole("STAFF");
        log.debug("Checking if user is STAFF: {}", result);
        return result;
    }

    public boolean isTechnician() {
        boolean result = hasRole("TECHNICIAN");
        log.debug("Checking if user is TECHNICIAN: {}", result);
        return result;
    }

    public boolean isCustomer() {
        boolean result = hasRole("CUSTOMER");
        log.debug("Checking if user is CUSTOMER: {}", result);
        return result;
    }

    public boolean isStaffOrAdmin() {
        boolean result = isStaff() || isAdmin();
        log.debug("Checking if user is STAFF or ADMIN: {}", result);
        return result;
    }

    private boolean hasRole(String role) {
        String userRole = currentUserService.getCurrentUserRole();
        boolean hasRole = userRole.equals(role);
        log.trace("User role: {}, checking against: {}, result: {}", userRole, role, hasRole);
        return hasRole;
    }

    public void checkRoleEditable(long roleId) {
        log.debug("Checking if role {} is editable by current user", roleId);
        long currentUserRoleId = currentUserService.getCurrentUserRoleId();

        int result = roleRepo.canRoleEdit(currentUserRoleId, roleId);

        if (result != 1) {
            log.warn("User with roleId {} attempted to edit roleId {} - FORBIDDEN",
                    currentUserRoleId, roleId);
            throw new CommonException.Forbidden(
                    "Bạn không được cấp phép để truy cập tài nguyên này"
            );
        }

        log.info("Role {} is editable by user with roleId {}", roleId, currentUserRoleId);
    }

    public void checkRoleEditable(String roleDisplayName) {
        log.debug("Checking if role with display name '{}' is editable", roleDisplayName);

        Role role = roleRepo.findByDisplayName(roleDisplayName)
                .orElseThrow(() -> {
                    log.error("Role not found with display name: {}", roleDisplayName);
                    return new CommonException.NotFound("Role with display name", roleDisplayName);
                });

        log.debug("Found role: id={}, displayName={}", role.getId(), roleDisplayName);
        checkRoleEditable(role.getId());
    }

    public void checkRoleEditable(Role role) {
        log.debug("Checking if role is editable: id={}", role.getId());

        roleRepo.findById(role.getId())
                .orElseThrow(() -> {
                    log.error("Role not found with id: {}", role.getId());
                    return new CommonException.NotFound("Không tìm thấy role");
                });

        checkRoleEditable(role.getId());
    }

}

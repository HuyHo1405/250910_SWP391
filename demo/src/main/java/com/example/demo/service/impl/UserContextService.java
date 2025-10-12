package com.example.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserContextService {

    //
    private final CurrentUserService currentUserService;

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isStaff() {
        return hasRole("STAFF");
    }

    public boolean isTechnician() {
        return hasRole("TECHNICIAN");
    }

    public boolean isCustomer() {
        return hasRole("CUSTOMER");
    }

    public boolean isStaffOrAdmin() {
        return isStaff() || isAdmin();
    }

    private boolean hasRole(String role) {
        String userRole = currentUserService.getCurrentUserRole();
        return userRole.equals(role);
    }
}

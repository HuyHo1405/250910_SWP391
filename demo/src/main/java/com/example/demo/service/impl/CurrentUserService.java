package com.example.demo.service.impl;

import com.example.demo.exception.UserException;
import com.example.demo.exception.UserException.UserNotFound;
import com.example.demo.model.entity.User;
import com.example.demo.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepo userRepo;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        String email = auth.getName(); // từ JWT
        return userRepo.findByEmailAddress(email)
                .orElseThrow(UserException.UserNotFound::new);
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getEmailAddress();
    }

    public boolean isStaffOrAdmin() {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }
        String roleName = user.getRole().getName().toUpperCase();
        return "ADMIN".equals(roleName) || "STAFF".equals(roleName);
    }

    public boolean isTechnician() {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }
        String roleName = user.getRole().getName().toUpperCase();
        return "TECHNICIAN".equals(roleName);
    }

    public boolean isCustomer() {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }
        String roleName = user.getRole().getName().toUpperCase();
        return "CUSTOMER".equals(roleName);
    }

}

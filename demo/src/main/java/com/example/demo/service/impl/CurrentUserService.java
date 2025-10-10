package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.entity.User;
import com.example.demo.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrentUserService {

    private final UserRepo userRepo;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        String email = auth.getName();
        return userRepo.findByEmailAddress(email)
                .orElseThrow(() -> new CommonException.NotFound("User", email));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getEmailAddress();
    }

    public String getCurrentUserRole() {
        User user = getCurrentUser();
        return user.getRole() != null ? user.getRole().getName() : null;
    }
}

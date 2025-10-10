package com.example.demo.controller;

import com.example.demo.model.dto.UserDTO;
import com.example.demo.service.interfaces.IUserProfileService;
import com.example.demo.security.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/userprofile")
@Tag(name = "User Profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final IUserProfileService userProfileService;
    private final JwtUtil jwtUtil;

    // ========== READ - Xem profile bản thân ==========
    @GetMapping("/me/profile")
    public ResponseEntity<UserDTO> getMyProfile(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        return ResponseEntity.ok(userProfileService.getProfileById(userId));
    }

    // ========== UPDATE - Chỉnh sửa thông tin bản thân ==========
    @PutMapping("/me/updateprofile")
    public ResponseEntity<UserDTO> updateMyProfile(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        userProfileService.updateProfile(userId, userDTO.getFullName(), userDTO.getEmail(), userDTO.getPhoneNumber());
        return ResponseEntity.ok(userProfileService.getProfileById(userId));
    }

    // ========== DELETE - Xóa thông tin profile bản thân ==========
    @DeleteMapping("/me/deleteprofile")
    public ResponseEntity<UserDTO> deleteMyProfileInfo(@RequestBody(required = false) UserDTO fieldsToDelete, HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);

        if (fieldsToDelete == null) {
            // Xóa toàn bộ thông tin profile
            userProfileService.deleteProfile(userId, "", "", "");
        } else {
            // Xóa chỉ các field được chỉ định
            String fullName = fieldsToDelete.getFullName() != null ? fieldsToDelete.getFullName() : null;
            String email = fieldsToDelete.getEmail() != null ? fieldsToDelete.getEmail() : null;
            String phoneNumber = fieldsToDelete.getPhoneNumber() != null ? fieldsToDelete.getPhoneNumber() : null;

            userProfileService.deleteProfile(userId, fullName, email, phoneNumber);
        }

        return ResponseEntity.ok(userProfileService.getProfileById(userId));
    }

    // Helper method để lấy userId từ JWT token
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractUserId(token);
        }
        throw new RuntimeException("JWT token not found in Authorization header");
    }
}
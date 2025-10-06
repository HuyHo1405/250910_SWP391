package com.example.demo.service.impl;

import com.example.demo.model.dto.UserDTO;
import com.example.demo.model.entity.EntityStatus;
import com.example.demo.model.entity.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.interfaces.IUserProfileService;
import com.example.demo.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService implements IUserProfileService {

    private final UserRepo userRepo;

    // ========== READ ==========

    @Override
    public UserDTO getProfileById(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new UserException("USER_NOT_FOUND", "User not found with ID: " + userId, HttpStatus.NOT_FOUND));

        return convertToDTO(user);
    }

    public List<UserDTO> getAllProfiles() {
        List<User> users = userRepo.findAll();
        return users.stream()
            .map(this::convertToDTO)
            .toList();
    }

    public List<UserDTO> getActiveProfiles() {
        return userRepo.findByStatus(EntityStatus.ACTIVE, null)
            .getContent()
            .stream()
            .map(this::convertToDTO)
            .toList();
    }

    // ========== UPDATE ==========

    @Override
    public void updateProfile(Long userId, String fullName, String email, String phoneNumber) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new UserException("USER_NOT_FOUND", "User not found with ID: " + userId, HttpStatus.NOT_FOUND));

        // Update fields if provided
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
        }
        if (email != null && !email.trim().isEmpty()) {
            // Check if email already exists for another user
            Optional<User> existingUser = userRepo.findByEmailAddress(email);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new UserException("EMAIL_ALREADY_EXISTS", "Email already exists: " + email, HttpStatus.CONFLICT);
            }
            user.setEmailAddress(email.trim());
        }
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            // Check if phone number already exists for another user
            Optional<User> existingUser = userRepo.findByPhoneNumber(phoneNumber);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new UserException("PHONE_ALREADY_EXISTS", "Phone number already exists: " + phoneNumber, HttpStatus.CONFLICT);
            }
            user.setPhoneNumber(phoneNumber.trim());
        }

        user.setUpdateAt(LocalDateTime.now());
        userRepo.save(user);
    }

    public UserDTO updateCompleteProfile(Long userId, UserDTO userDTO) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new UserException("USER_NOT_FOUND", "User not found with ID: " + userId, HttpStatus.NOT_FOUND));

        // Update all fields
        user.setFullName(userDTO.getFullName());
        user.setEmailAddress(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setUpdateAt(LocalDateTime.now());

        User savedUser = userRepo.save(user);
        return convertToDTO(savedUser);
    }

    // ========== DELETE ==========

    @Override
    public void deleteProfile(Long userId, String fullName, String email, String phoneNumber) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new UserException("USER_NOT_FOUND", "User not found with ID: " + userId, HttpStatus.NOT_FOUND));

        // Selective deletion - clear specific fields if provided
        if (fullName != null) {
            user.setFullName("");
        }
        if (email != null) {
            user.setEmailAddress("");
        }
        if (phoneNumber != null) {
            user.setPhoneNumber("");
        }

        // If all main fields are to be cleared, set status to INACTIVE
        if (fullName != null && email != null && phoneNumber != null) {
            user.setStatus(EntityStatus.INACTIVE);
        }

        user.setUpdateAt(LocalDateTime.now());
        userRepo.save(user);
    }

    // Delete user by ID (hard delete)
    public void deleteProfileById(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserException("USER_NOT_FOUND", "User not found with ID: " + userId, HttpStatus.NOT_FOUND);
        }
        userRepo.deleteById(userId);
    }

    // Soft delete - set status to INACTIVE
    public void softDeleteProfile(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new UserException("USER_NOT_FOUND", "User not found with ID: " + userId, HttpStatus.NOT_FOUND));

        user.setStatus(EntityStatus.INACTIVE);
        user.setUpdateAt(LocalDateTime.now());
        userRepo.save(user);
    }

    // Delete all profiles (hard delete)
    public void deleteAllProfiles() {
        userRepo.deleteAll();
    }

    // Soft delete all profiles
    public void softDeleteAllProfiles() {
        List<User> users = userRepo.findAll();
        users.forEach(user -> {
            user.setStatus(EntityStatus.INACTIVE);
            user.setUpdateAt(LocalDateTime.now());
        });
        userRepo.saveAll(users);
    }

    // Delete profiles by status
    public void deleteProfilesByStatus(EntityStatus status) {
        List<User> users = userRepo.findByStatus(status, null).getContent();
        userRepo.deleteAll(users);
    }

    // ========== MANAGE STATUS ==========

    @Override
    public void manageProfileStatus(Long userId, String action) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new UserException("USER_NOT_FOUND", "User not found with ID: " + userId, HttpStatus.NOT_FOUND));

        switch (action.toUpperCase()) {
            case "ACTIVATE":
                user.setStatus(EntityStatus.ACTIVE);
                break;
            case "DEACTIVATE":
                user.setStatus(EntityStatus.INACTIVE);
                break;
            case "DELETE":
                userRepo.deleteById(userId);
                return;
            default:
                throw new UserException("INVALID_ACTION", "Invalid action: " + action, HttpStatus.BAD_REQUEST);
        }

        user.setUpdateAt(LocalDateTime.now());
        userRepo.save(user);
    }

    // ========== HELPER METHODS ==========

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .email(user.getEmailAddress())
            .fullName(user.getFullName())
            .phoneNumber(user.getPhoneNumber())
            .role(user.getRole().getName())
            .status(user.getStatus().name())
            .createdAt(user.getCreatedAt())
            .lastLogin(user.getLoginAt())
            .build();
    }

    // Restore deleted fields
    public void restoreProfile(Long userId, String fullName, String email, String phoneNumber) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new UserException("USER_NOT_FOUND", "User not found with ID: " + userId, HttpStatus.NOT_FOUND));

        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmailAddress(email.trim());
        }
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            user.setPhoneNumber(phoneNumber.trim());
        }

        user.setStatus(EntityStatus.ACTIVE);
        user.setUpdateAt(LocalDateTime.now());
        userRepo.save(user);
    }
}

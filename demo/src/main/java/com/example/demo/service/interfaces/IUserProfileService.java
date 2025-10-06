package com.example.demo.service.interfaces;

import com.example.demo.model.dto.UserDTO;

public interface IUserProfileService {

    // ========== READ ==========

    UserDTO getProfileById(Long userId);

    // ========== UPDATE ==========
    void updateProfile(Long userId, String fullName, String email, String phoneNumber);

    // ========== DELETE ==========
    void deleteProfile(Long userId, String fullName, String email, String phoneNumber);

    // ========== MANAGE STATUS ==========
    void manageProfileStatus(Long userId, String action);
}
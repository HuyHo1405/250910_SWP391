package com.example.demo.service.interfaces;

import com.example.demo.model.dto.AuthRequest;
import com.example.demo.model.dto.AuthResponse;
import com.example.demo.model.dto.UserProfileRequest;

public interface IPasswordService {
    String forgotPassword(AuthRequest.ForgotPassword request);
    String resetPassword(AuthRequest.ResetPassword request);
    String updatePassword(Long userId, UserProfileRequest.Password request);
    String generatePassword();
}

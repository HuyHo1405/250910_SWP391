package com.example.demo.service.interfaces;

import com.example.demo.model.dto.AuthRequest;
import com.example.demo.model.dto.AuthResponse;

public interface IPasswordService {
    public String forgotPassword(AuthRequest.ForgotPassword request);
    public String resetPassword(AuthRequest.ResetPassword request);
}

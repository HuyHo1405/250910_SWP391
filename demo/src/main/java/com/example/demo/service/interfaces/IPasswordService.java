package com.example.demo.service.interfaces;

import com.example.demo.model.dto.AuthRequest;
import com.example.demo.model.dto.AuthResponse;

public interface IPasswordService {
    public AuthResponse forgotPassword(AuthRequest.ForgotPassword request);
    public AuthResponse resetPassword(AuthRequest.ResetPassword request);
}

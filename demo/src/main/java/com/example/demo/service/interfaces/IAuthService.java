package com.example.demo.service.interfaces;

import com.example.demo.model.dto.*;

public interface IAuthService {
    public AuthResponse register(AuthRequest.Register registerRequest);
    public AuthResponse login(String email, String password);
    public AuthResponse verifyEmail(AuthRequest.Verify verifyRequest);
    public AuthResponse logout(AuthRequest.Logout logoutRequest);
    public AuthResponse forgotPassword(AuthRequest.ForgotPassword forgotPasswordRequest);
    public AuthResponse resetPassword(AuthRequest.ResetPassword resetPasswordRequest);
}

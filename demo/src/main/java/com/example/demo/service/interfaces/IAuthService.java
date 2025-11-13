package com.example.demo.service.interfaces;

import com.example.demo.model.dto.*;

public interface IAuthService {
    public MessageResponse register(AuthRequest.Register registerRequest);
    public AuthResponse login(String email, String password);
    public MessageResponse resentVerificationCode(String email);
    public MessageResponse verifyEmail(AuthRequest.Verify verifyRequest);
    public MessageResponse logout(AuthRequest.Logout logoutRequest);
    public MessageResponse forgotPassword(AuthRequest.ForgotPassword forgotPasswordRequest);
    public MessageResponse resetPassword(AuthRequest.ResetPassword resetPasswordRequest);
}

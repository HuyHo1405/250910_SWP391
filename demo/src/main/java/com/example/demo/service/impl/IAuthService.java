package com.example.demo.service.impl;

import com.example.demo.model.dto.AuthResponse;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.model.dto.VerifyRequest;

public interface IAuthService {
    public AuthResponse register(RegisterRequest registerRequest);
    public AuthResponse login(String email, String password);
    public AuthResponse verifyEmail(VerifyRequest verifyRequest);
}

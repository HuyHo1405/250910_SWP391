package com.example.demo.service;

import com.example.demo.model.dto.AuthResponse;
import com.example.demo.model.dto.RegisterRequest;

public interface IAuthService {
    public AuthResponse register(RegisterRequest registerRequest);
    public AuthResponse login(String email, String password);
    public AuthResponse verifyEmail(String token);
}

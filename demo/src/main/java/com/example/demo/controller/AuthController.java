package com.example.demo.controller;

import com.example.demo.model.dto.*;
import com.example.demo.service.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthRequest.Register request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest.Login request) {
        return ResponseEntity.ok(authService.login(request.getUserName(), request.getPassword()));
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verifyEmail(@RequestBody @Valid AuthRequest.Verify request) {
        return ResponseEntity.ok(authService.verifyEmail(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestBody @Valid AuthRequest.Logout request) {
        return ResponseEntity.ok(authService.logout(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponse> forgotPassword(@RequestBody @Valid AuthRequest.ForgotPassword request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody @Valid AuthRequest.ResetPassword request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody AuthRequest.RefreshToken request) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}

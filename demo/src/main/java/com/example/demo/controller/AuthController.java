package com.example.demo.controller;

import com.example.demo.model.dto.AuthResponse;
import com.example.demo.model.dto.LoginRequest;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.model.dto.VerifyRequest;
import com.example.demo.service.impl.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request.getUserName(), request.getPassword()));
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verifyEmail(@RequestBody VerifyRequest request) {
        return ResponseEntity.ok(authService.verifyEmail(request));
    }
}

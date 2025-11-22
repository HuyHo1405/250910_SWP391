package com.example.demo.controller;

import com.example.demo.model.dto.*;
import com.example.demo.service.interfaces.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/register")
    @Operation(summary = "[PUBLIC] [GUEST] Register a new user", description = "Allows a guest to register a new account.")
    public ResponseEntity<MessageResponse> register(@RequestBody @Valid AuthRequest.Register request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "[PUBLIC] [GUEST] Login to the system", description = "Allows a guest to login and receive authentication tokens.")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest.Login request) {
        return ResponseEntity.ok(authService.login(request.getUserName(), request.getPassword()));
    }

    @PostMapping("/verify")
    @Operation(summary = "[PRIVATE] [GUEST] Verify email address", description = "Verifies a user's email address using a verification code.")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestBody @Valid AuthRequest.Verify request) {
        return ResponseEntity.ok(authService.verifyEmail(request));
    }

    @PostMapping("/resend-verification-code")
    @Operation(summary = "[PRIVATE] [GUEST] Resend verification code", description = "Resends the email verification code to the user's email address.")
    public ResponseEntity<MessageResponse> resendVerificationCode(@RequestParam @Email String email) {
        return ResponseEntity.ok(authService.resentVerificationCode(email));
    }

    @PostMapping("/logout")
    @Operation(summary = "[PRIVATE] [USER] Logout from the system", description = "Logs out the currently authenticated user.")
    public ResponseEntity<MessageResponse> logout(@RequestBody @Valid AuthRequest.Logout request) {
        return ResponseEntity.ok(authService.logout(request));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "[PUBLIC] [GUEST] Forgot password", description = "Initiates the password reset process for a user who forgot their password.")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody @Valid AuthRequest.ForgotPassword request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "[PRIVATE] [GUEST] Reset password", description = "Resets the user's password using a valid reset token.")
    public ResponseEntity<MessageResponse> resetPassword(
            @RequestBody @Valid AuthRequest.ResetPassword request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "[PRIVATE] [USER] Refresh authentication token", description = "Refreshes the authentication token for a logged-in user.")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody AuthRequest.RefreshToken request) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}

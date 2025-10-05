package com.example.demo.service.impl;

import com.example.demo.config.AppConfig;
import com.example.demo.exception.AuthException.InvalidToken;
import com.example.demo.exception.UserException.UserNotFound;
import com.example.demo.model.dto.AuthRequest;
import com.example.demo.model.dto.AuthResponse;
import com.example.demo.model.entity.ResetPasswordToken;
import com.example.demo.model.entity.User;
import com.example.demo.repo.ResetPasswordTokenRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.interfaces.IMailService;
import com.example.demo.service.interfaces.IPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordService implements IPasswordService {
    private static final int TOKEN_EXPIRATION_HOURS = 24;

    private final AppConfig appConfig;
    private final UserRepo userRepo;
    private final ResetPasswordTokenRepo tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final IMailService mailService;

    @Override
    @Transactional
    public AuthResponse forgotPassword(AuthRequest.ForgotPassword request) {
        // Find user by email
        User user = userRepo.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(UserNotFound::new);

        // Generate token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS);

        // Create and save token
        ResetPasswordToken resetToken = ResetPasswordToken.builder()
                .token(token)
                .userId(user.getId())
                .expiryDate(expiryDate)
                .revoked(false)
                .build();

        // Revoke any existing tokens
        tokenRepo.revokeUserTokens(user.getId());
        tokenRepo.save(resetToken);

        // Send email with reset link
        String resetLink = appConfig.getActiveUrl() + "/auth/reset-password?token=" + token;
        mailService.sendPasswordResetMail(user.getEmailAddress(), resetLink);

        return AuthResponse.builder()
                .message("Password reset link has been sent to your email")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse resetPassword(AuthRequest.ResetPassword request) {
        // Find the token
        ResetPasswordToken token = tokenRepo.findByToken(request.getToken())
                .orElseThrow(InvalidToken::new);

        // Validate token
        if (token.isRevoked() || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired or is no longer valid");
        }

        // Find user
        User user = userRepo.findById(token.getUserId())
                .orElseThrow(UserNotFound::new);

        // Update password
        user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        // Revoke the used token
        token.setRevoked(true);
        tokenRepo.save(token);

        return AuthResponse.builder()
                .message("Password has been reset successfully")
                .build();
    }
}
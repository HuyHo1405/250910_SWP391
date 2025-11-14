package com.example.demo.service.impl;

import com.example.demo.config.AppConfig;
import com.example.demo.exception.AuthException;
import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.AuthRequest;
import com.example.demo.model.dto.AuthResponse;
import com.example.demo.model.dto.UserProfileRequest;
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

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordService implements IPasswordService {

    private static final int TOKEN_EXPIRATION_HOURS = 24;

    private final IMailService mailService;
    private final AccessControlService accessControlService;

    private final UserRepo userRepo;
    private final ResetPasswordTokenRepo tokenRepo;

    private final PasswordEncoder passwordEncoder;
    private final AppConfig appConfig;

    @Override
    @Transactional
    public String forgotPassword(AuthRequest.ForgotPassword request) {
        // Find user by email
        User user = userRepo.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new CommonException.NotFound("User", request.getEmailAddress()));

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
        String resetLink = appConfig.getActiveUrl() + "/api/auth/reset-password?token=" + token;
        mailService.sendPasswordResetMail(user.getEmailAddress(), resetLink);

        return "Password reset link has been sent to your email";
    }

    @Override
    @Transactional
    public String resetPassword(AuthRequest.ResetPassword request) {
        // Find the token
        ResetPasswordToken token = tokenRepo.findByToken(request.getToken())
                .orElseThrow(AuthException.InvalidToken::new);

        // Validate token
        if (token.isRevoked() || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AuthException.TokenExpired();
        }

        // Find user
        User user = userRepo.findById(token.getUserId())
                .orElseThrow(() -> new CommonException.NotFound("User", token.getUserId()));

        // Update password
        user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        // Revoke the used token
        token.setRevoked(true);
        tokenRepo.save(token);

        return "Password reset successful. Please login with your new password";
    }

    @Override
    public String updatePassword(Long userId, UserProfileRequest.Password request) {

        accessControlService.verifyResourceAccess(userId, "USER", "update");

        boolean isOldPassword = request.getOldPassword().equals(request.getNewPassword());
        if(isOldPassword) {
           throw new CommonException.InvalidOperation("Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new CommonException.NotFound("User", userId));
        user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        return "Password reset successful. Please login with your new password";
    }

    @Override
    public String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#!$";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }

}

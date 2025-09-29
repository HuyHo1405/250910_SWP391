package com.example.demo.service.impl;

import com.example.demo.exception.AuthException.*;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.VerificationCode;
import com.example.demo.repo.VerificationCodeRepo;
import com.example.demo.service.interfaces.IVerificationCodeService;
import com.example.demo.utils.CodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeService implements IVerificationCodeService {

    private static final int CODE_EXPIRATION_MINUTES = 15;

    private final VerificationCodeRepo verificationCodeRepo;

    @Override
    @Transactional
    public String addVerificationCode(User user) {
        try {
            // Invalidate any existing codes for this user to maintain one active code per user
            verificationCodeRepo.invalidateUserCodes(user.getId());

            // Generate a new 6-digit numeric verification code
            String verificationCode = CodeGenerator.generateNumericCode(6);

            // Create and save new verification code with 15-minute expiration
            VerificationCode verificationCodeEntity = VerificationCode.builder()
                    .user(user)
                    .code(verificationCode)
                    .expiresAt(LocalDateTime.now().plus(CODE_EXPIRATION_MINUTES, ChronoUnit.MINUTES))
                    .used(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            verificationCodeRepo.save(verificationCodeEntity);
            log.info("Verification code generated and saved for user: {}", user.getEmailAddress());
            return verificationCode;

        } catch (Exception e) {
            log.error("Failed to generate and save verification code for user: {}", user.getEmailAddress(), e);
            throw new CodeGenerationFailed();
        }
    }

    @Override
    @Transactional
    public void verifyCode(User user, String code) {
        VerificationCode verificationCode = verificationCodeRepo
                    .findValidVerificationCode(user, code, LocalDateTime.now())
                    .orElseThrow(VerificationCodeInvalid::new);

        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new VerificationCodeExpired();
        }

        if (verificationCode.isUsed()) {
            throw new VerificationCodeInvalid();
        }

        // Mark code as used
        verificationCode.setUsed(true);
        verificationCodeRepo.save(verificationCode);

        // Cleanup expired codes
        cleanupExpiredCodes();

        log.info("Verification successful for user: {}", user.getEmailAddress());
    }

    @Transactional
    public int cleanupExpiredCodes() {
        int deletedCount = verificationCodeRepo.deleteExpiredCodes(LocalDateTime.now());
        if (deletedCount > 0) {
            log.info("Cleaned up {} expired verification codes", deletedCount);
        }
        return deletedCount;
    }
}
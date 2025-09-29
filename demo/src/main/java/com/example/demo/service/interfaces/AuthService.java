package com.example.demo.service.interfaces;

import com.example.demo.exception.AuthException.*;
import com.example.demo.exception.UserException.*;
import com.example.demo.security.JwtUtil;
import com.example.demo.model.dto.AuthResponse;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.model.dto.VerifyRequest;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.EntityStatus;
import com.example.demo.repo.RoleRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.impl.IAuthService;
import com.example.demo.service.impl.IMailService;
import com.example.demo.service.impl.IVerificationCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final IVerificationCodeService verificationCodeService;
    private final IMailService mailService;
    private final UserValidationService userValidationService;

    @Override
    @Transactional
    public AuthResponse register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Starting registration process for email: {}", registerRequest.getEmailAddress());

        userValidationService.checkEmailAndPhoneAvailability(
                registerRequest.getEmailAddress(),
                registerRequest.getPhoneNumber());

        User savedUser = createAndSaveUser(registerRequest);

        return buildAuthResponse(savedUser, false);
    }

    @Override
    @Transactional
    public AuthResponse login(String email, String password) {
        User user = userRepo.findByEmailAddress(email)
                .orElseThrow(UserNotFound::new);

        if (user.getStatus() == EntityStatus.ARCHIVED) {
            throw new AccountBlocked();
        }

        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new InvalidCredentials();
        }

        if (user.getStatus() == EntityStatus.INACTIVE) {
            // Generate and send verification code
            String verificationCode = verificationCodeService.addVerificationCode(user);
            mailService.sendVerificationEmail(user.getEmailAddress(), verificationCode);

            return AuthResponse.builder()
                    .requiresVerification(true)
                    .message("Verification code sent to your email")
                    .build();
        }

        updateLoginTimestamp(user);
        return buildAuthResponse(user, true);
    }

    @Override
    @Transactional
    public AuthResponse verifyEmail(@Valid @RequestBody VerifyRequest verifyRequest) {
        String email = verifyRequest.getEmailAddress();
        String code = verifyRequest.getCode();

        User user = userRepo.findByEmailAddress(email)
                .orElseThrow(UserNotFound::new);

        verificationCodeService.verifyCode(user, code);

        // Activate user
        user.setStatus(EntityStatus.ACTIVE);
        user.setUpdateAt(LocalDateTime.now());
        userRepo.save(user);

        return buildAuthResponse(user, true);
    }

    // Helper methods
    private User createAndSaveUser(RegisterRequest request) {
        Role customerRole = roleRepo.findByName("CUSTOMER")
                .orElseThrow(RoleNotFound::new);

        User newUser = User.builder()
                .fullName(request.getFullName())
                .emailAddress(request.getEmailAddress())
                .phoneNumber(request.getPhoneNumber())
                .hashedPassword(passwordEncoder.encode(request.getPassword()))
                .role(customerRole)
                .status(EntityStatus.INACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        return userRepo.save(newUser);
    }

    private void updateLoginTimestamp(User user) {
        user.setLoginAt(LocalDateTime.now());
        userRepo.save(user);
    }

    private AuthResponse buildAuthResponse(User user, boolean verified) {
        return AuthResponse.builder()
                .token(verified ? jwtUtil.generateToken(user) : null)
                .emailAddress(user.getEmailAddress())
                .roleId(user.getRole().getId())
                .roleName(user.getRole().getName())
                .isVerified(verified)
                .requiresVerification(!verified)
                .build();
    }
}

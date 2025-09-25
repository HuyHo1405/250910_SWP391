package com.example.demo.service;

import com.example.demo.config.JwtUtil;
import com.example.demo.exception.AuthException.*;
import com.example.demo.model.dto.AuthResponse;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.EntityStatus;
import com.example.demo.model.entity.VerificationToken;
import com.example.demo.repo.RoleRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.repo.VerificationTokenRepo;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private VerificationTokenRepo verificationTokenRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    //main flow methods
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Starting registration process for email: {}", registerRequest.getEmailAddress());

        checkEmailAndPhoneAvailability(registerRequest.getEmailAddress(), registerRequest.getPhoneNumber());
        User savedUser = createAndSaveUser(registerRequest);
        sendVerificationEmail(savedUser);

        return buildAuthResponse(savedUser, false);
    }

    @Transactional
    public AuthResponse login(String email, String password) {
        User user = userRepo.findByEmailAddress(email)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new InvalidCredentialsException();
        }

        if (user.getStatus() == EntityStatus.INACTIVE) {
            handleUnverifiedLogin(user);
        }

        updateLoginTimestamp(user);
        return buildAuthResponse(user, true);
    }

    @Transactional
    public AuthResponse verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        validateVerificationToken(verificationToken);
        User user = activateUser(verificationToken);

        return buildAuthResponse(user, true);
    }

    //small helper methods
    private void checkEmailAndPhoneAvailability(String email, String phone) {
        if (userRepo.findByEmailAddress(email).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        if (userRepo.findByPhoneNumber(phone).isPresent()) {
            throw new PhoneNumberAlreadyExistsException();
        }
    }

    private User createAndSaveUser(RegisterRequest request) {
        Role customerRole = roleRepo.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Default customer role not found"));

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

    private void sendVerificationEmail(User user) {
        try {
            String token = generateVerificationToken(user);
            emailService.sendVerificationEmail(user.getEmailAddress(), token);
            log.info("Verification email sent to: {}", user.getEmailAddress());
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}: {}", user.getEmailAddress(), e.getMessage());
            throw new EmailSendFailedException();
        }
    }

    private void handleUnverifiedLogin(User user) {
        verificationTokenRepo.findByUserIdAndUsed(user.getId(), false)
                .orElseGet(() -> {
                    sendVerificationEmail(user);
                    return null;
                });

        throw new UnverifiedAccountException();
    }

    private void validateVerificationToken(VerificationToken token) {
        if (token.isUsed()) {
            throw new InvalidTokenException("Token has already been used");
        }
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token has expired");
        }
    }

    private User activateUser(VerificationToken verificationToken) {
        User user = verificationToken.getUser();
        user.setStatus(EntityStatus.ACTIVE);
        user.setUpdateAt(LocalDateTime.now());
        userRepo.save(user);

        verificationToken.setUsed(true);
        verificationTokenRepo.save(verificationToken);

        return user;
    }

    private void updateLoginTimestamp(User user) {
        user.setLoginAt(LocalDateTime.now());
        userRepo.save(user);
    }

    private AuthResponse buildAuthResponse(User user, boolean verified) {
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user))
                .emailAddress(user.getEmailAddress())
                .roleId(user.getRole().getId())
                .roleName(user.getRole().getName())
                .isVerified(verified)
                .requiresVerification(!verified)
                .build();
    }

    private String generateVerificationToken(User user) {
        VerificationToken verificationToken = VerificationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        verificationTokenRepo.save(verificationToken);
        return verificationToken.getToken();
    }
}

package com.example.demo.service.impl;

import com.example.demo.exception.AuthException;
import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entity.RefreshToken;
import com.example.demo.repo.RefreshTokenRepo;
import com.example.demo.security.JwtUtil;
import com.example.demo.model.entity.Role;
import com.example.demo.model.entity.User;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.repo.RoleRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.interfaces.IAuthService;
import com.example.demo.service.interfaces.IMailService;
import com.example.demo.service.interfaces.IPasswordService;
import com.example.demo.service.interfaces.IVerificationCodeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final IVerificationCodeService verificationCodeService;
    private final IMailService mailService;
    private final IPasswordService passwordService;
    private final UserValidationService userValidationService;

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final RefreshTokenRepo refreshTokenRepo;

    private final HttpServletRequest httpServletRequest;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MessageResponse register(AuthRequest.Register registerRequest) {
        log.info("Starting registration process for email: {}", registerRequest.getEmailAddress());

        userValidationService.checkEmailAndPhoneAvailability(
                registerRequest.getEmailAddress(),
                registerRequest.getPhoneNumber());

        User savedUser = createAndSaveUser(registerRequest);

        String verificationCode = verificationCodeService.addVerificationCode(savedUser);
        mailService.sendVerificationMail(savedUser.getEmailAddress(), verificationCode);

        // Return simple success message
        return MessageResponse.builder()
                .message("Đăng ký thành công. Vui lòng kiểm tra email để xác thực tài khoản.")
                .timestamp(LocalDateTime.now())
                .path("uri=" + httpServletRequest.getRequestURI())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(String email, String password) {
        User user = userRepo.findByEmailAddress(email)
                .orElseThrow(() -> new CommonException.NotFound("User", email));

        if (user.getStatus() == EntityStatus.ARCHIVED) {
            throw new AuthException.AccountBlocked();
        }

        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new AuthException.InvalidCredentials();
        }

        if (user.getStatus() == EntityStatus.INACTIVE) {
            // Generate and send verification code
            if (verificationCodeService.isExpiredOrMissing(user)) {
                // Nếu hết hạn hoặc không có, tạo mã mới và gửi lại
                String newVerificationCode = verificationCodeService.addVerificationCode(user);
                mailService.sendVerificationMail(user.getEmailAddress(), newVerificationCode);
                return AuthResponse.builder()
                        .requiresVerification(true)
                        .message("Tài khoản chưa xác thực hoặc mã hết hạn. Đã gửi lại mã xác thực đến email.")
                        .build();
            } else {
                return AuthResponse.builder()
                        .requiresVerification(true)
                        .message("Tài khoản chưa xác thực. Vui lòng kiểm tra email để lấy mã xác thực.")
                        .build();
            }
        }

        updateLoginTimestamp(user);

        // Revoke all existing refresh tokens for this user
        refreshTokenRepo.revokeAllUserTokens(user.getId());

        // Generate new tokens
        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public MessageResponse verifyEmail(AuthRequest.Verify verifyRequest) {
        String email = verifyRequest.getUserName();
        String code = verifyRequest.getCode();

        User user = userRepo.findByEmailAddress(email)
                .orElseThrow(() -> new CommonException.NotFound("User", email));

        if(user.getStatus() == EntityStatus.ACTIVE){
            throw new CommonException.InvalidOperation("Tài khoản đã được xác thực trước đó.");
        }

        verificationCodeService.verifyCode(user, code);

        // Activate user
        user.setStatus(EntityStatus.ACTIVE);
        user.setUpdateAt(LocalDateTime.now());
        userRepo.save(user);

        return MessageResponse.builder()
                .message("Xác thực email thành công. Bạn có thể đăng nhập ngay bây giờ.")
                .timestamp(LocalDateTime.now())
                .path("uri=" + httpServletRequest.getRequestURI())
                .build();
    }

    @Override
    @Transactional
    public MessageResponse resentVerificationCode(String email) {
        User user = userRepo.findByEmailAddress(email)
                .orElseThrow(() -> new CommonException.NotFound("User", email));

        if(user.getStatus() == EntityStatus.ACTIVE){
            throw new CommonException.InvalidOperation("Tài khoản đã được xác thực trước đó.");
        }

        String newVerificationCode = verificationCodeService.addVerificationCode(user);
        mailService.sendVerificationMail(user.getEmailAddress(), newVerificationCode);

        return MessageResponse.builder()
                .message("Đã gửi lại mã xác thực đến email của bạn.")
                .timestamp(LocalDateTime.now())
                .path("uri=" + httpServletRequest.getRequestURI())
                .build();
    }


    @Override
    @Transactional
    public MessageResponse logout(AuthRequest.Logout logoutRequest) {
        String refreshTokenStr = logoutRequest.getRefreshToken();

        // Find the refresh token
        RefreshToken refreshToken = refreshTokenRepo.findByToken(refreshTokenStr)
                .orElseThrow(AuthException.InvalidToken::new);

        // Revoke the token
        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);

        // Return success response
        return MessageResponse.builder()
                .message("Logout successful")
                .timestamp(LocalDateTime.now())
                .path("uri=" + httpServletRequest.getRequestURI())
                .build();
    }

    @Override
    public MessageResponse forgotPassword(AuthRequest.ForgotPassword forgotPasswordRequest) {
        return MessageResponse.builder()
                .message(passwordService.forgotPassword(forgotPasswordRequest))
                .timestamp(LocalDateTime.now())
                .path("uri=" + httpServletRequest.getRequestURI())
                .build();
    }

    @Override
    public MessageResponse resetPassword(AuthRequest.ResetPassword resetPasswordRequest) {
        return MessageResponse.builder()
                .message(passwordService.resetPassword(resetPasswordRequest))
                .timestamp(LocalDateTime.now())
                .path("uri=" + httpServletRequest.getRequestURI())
                .build();
    }

    // Helper methods
    private User createAndSaveUser(AuthRequest.Register request) {
        Role customerRole = roleRepo.findByName("CUSTOMER")
                .orElseThrow(() -> new CommonException.NotFound("Role", "CUSTOMER"));

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

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepo.findByToken(refreshToken)
                .orElseThrow(AuthException.InvalidToken::new);

        if (token.isRevoked() || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AuthException.TokenExpired();
        }

        User user = userRepo.findById(token.getUserId())
                .orElseThrow(() -> new CommonException.NotFound("User", token.getUserId()));

        if (user.getStatus() != EntityStatus.ACTIVE) {
            throw new AuthException.AccountBlocked();
        }

        // Generate new tokens
        String newAccessToken = jwtUtil.generateToken(user);
        String newRefreshToken = createRefreshToken(user);

        // Revoke the old refresh token
        refreshTokenRepo.revokeAllUserTokens(user.getId());

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }

    private String createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .token(jwtUtil.generateRefreshToken(user))
                .expiryDate(LocalDateTime.now().plusDays(7)) // 7 days expiry
                .revoked(false)
                .build();

        refreshToken = refreshTokenRepo.save(refreshToken);
        return refreshToken.getToken();
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        UserProfileResponse userInfo = UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmailAddress())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .roleDisplayName(user.getRole().getDisplayName())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLoginAt())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresInSec(jwtUtil.getExpirationInSeconds())
                .user(userInfo)
                .build();
    }

}

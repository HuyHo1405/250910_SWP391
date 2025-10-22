package com.example.demo.controller;

import com.example.demo.model.dto.*;
import com.example.demo.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register new user account",
            description = "Create new user with CUSTOMER role. Email and phone must be unique."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Registration successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    value = """
                {
                  "message": "Registration successful.",
                  "timestamp": "2025-10-22T13:19:01.4965807",
                  "path": "uri=/api/auth/register"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input - validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "INVALID_INPUT",
                                              "message": "emailAddress: Email address must be valid",
                                              "timestamp": "2025-10-22T13:58:34.557505",
                                              "path": "uri=/api/auth/register"
                                            }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Role CUSTOMER not found in system",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "NOT_FOUND",
                  "message": "Role not found: CUSTOMER",
                  "timestamp": "2025-10-22T13:19:01.4965807",
                  "path": "uri=/api/auth/register"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email or phone already exists",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Email exists",
                                            value = """
                    {
                      "code": "ALREADY_EXISTS",
                      "message": "User already exists with email: user@example.com",
                      "timestamp": "2025-10-22T13:19:01.4965807",
                      "path": "uri=/api/auth/register"
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "Phone exists",
                                            value = """
                    {
                      "code": "ALREADY_EXISTS",
                      "message": "User already exists with phoneNumber: 0123456789",
                      "timestamp": "2025-10-22T13:19:01.4965807",
                      "path": "uri=/api/auth/register"
                    }
                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "INTERNAL_ERROR",
                  "message": "An unexpected error occurred",
                  "timestamp": "2025-10-22T13:19:01.4965807",
                  "path": "uri=/api/auth/register"
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<MessageResponse> register(@RequestBody @Valid AuthRequest.Register request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticate user with email and password. Returns JWT tokens (access + refresh) for verified accounts."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful - Returns JWT tokens and user info",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples ={
                                    @ExampleObject(
                                    name = "Success - Verified account",
                                    value = """
                {
                  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQ1VTVE9NRVIiLCJ0eXBlIjoiYWNjZXNzIiwic3ViIjoidXNlckBleGFtcGxlLmNvbSIsImlhdCI6MTc2MTExNjg2NSwiZXhwIjoxNzYxMTE3NzY1fQ.h84Bg7MdUuPI7-fivxXSLx5xO6noMQNbQdlRCGsz_dE",
                  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoicmVmcmVzaCIsInN1YiI6InVzZXJAZXhhbXBsZS5jb20iLCJpYXQiOjE3NjExMTY4NjUsImV4cCI6MTc2MTcyMTY2NX0.iRBqijfeOElAJ47l05ZanuDQte8HjLwb-mVBgtEQi3c",
                  "expiresIn": 900,
                  "tokenType": "Bearer",
                  "user": {
                    "id": 5,
                    "email": "user@example.com",
                    "fullName": "Nguyen Van E",
                    "phoneNumber": "0905678901",
                    "role": "CUSTOMER",
                    "status": "ACTIVE",
                    "createdAt": "2025-10-22 14:04:55",
                    "lastLogin": "2025-10-22 14:07:45"
                  },
                  "requiresVerification": false,
                  "message": null
                }
                """
                                    ),
                                    @ExampleObject(
                                            name = "Unverified account",
                                            value = """
                    {
                      "accessToken": null,
                      "refreshToken": null,
                      "expiresIn": null,
                      "tokenType": null,
                      "user": null,
                      "requiresVerification": true,
                      "message": "Your account has not verified yet. Verification code sent to your email"
                    }
                """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "BAD_REQUEST",
                  "message": "Email format is invalid",
                  "timestamp": "2025-10-22T14:07:45",
                  "path": "uri=/api/auth/login"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "INVALID_CREDENTIALS",
                  "message": "Invalid username or password",
                  "timestamp": "2025-10-22T14:07:45",
                  "path": "uri=/api/auth/login"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Account is blocked",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "ACCOUNT_BLOCKED",
                  "message": "Account is blocked",
                  "timestamp": "2025-10-22T14:07:45",
                  "path": "uri=/api/auth/login"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - User does not exist",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "NOT_FOUND",
                  "message": "User not found: user@example.com",
                  "timestamp": "2025-10-22T14:07:45",
                  "path": "uri=/api/auth/login"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service unavailable - Email service failed",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "INTERNAL_ERROR",
                  "message": "An unexpected error occurred",
                  "timestamp": "2025-10-22T13:19:01.4965807",
                  "path": "uri=/api/auth/login"
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest.Login request) {
        return ResponseEntity.ok(authService.login(request.getUserName(), request.getPassword()));
    }

    @PostMapping("/verify")
    @Operation(
            summary = "Verify email with code",
            description = "Verify user email with 6-digit code sent to email. Account status changes from INACTIVE to ACTIVE after successful verification."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email verified successfully - Returns JWT tokens",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Success - Email verified",
                                    value = """
                {
                  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQ1VTVE9NRVIiLCJ0eXBlIjoiYWNjZXNzIiwic3ViIjoidXNlckBleGFtcGxlLmNvbSIsImlhdCI6MTc2MTExNjg2NSwiZXhwIjoxNzYxMTE3NzY1fQ.h84Bg7MdUuPI7-fivxXSLx5xO6noMQNbQdlRCGsz_dE",
                  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoicmVmcmVzaCIsInN1YiI6InVzZXJAZXhhbXBsZS5jb20iLCJpYXQiOjE3NjExMTY4NjUsImV4cCI6MTc2MTcyMTY2NX0.iRBqijfeOElAJ47l05ZanuDQte8HjLwb-mVBgtEQi3c",
                  "expiresIn": 900,
                  "tokenType": "Bearer",
                  "user": {
                    "id": 5,
                    "email": "user@example.com",
                    "fullName": "Nguyen Van E",
                    "phoneNumber": "0905678901",
                    "role": "CUSTOMER",
                    "status": "ACTIVE",
                    "createdAt": "2025-10-22 14:04:55",
                    "lastLogin": "2025-10-22 14:20:30"
                  },
                  "requiresVerification": false,
                  "message": null
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid input or code issue",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Code expired",
                                            value = """
                    {
                      "code": "CODE_INVALID",
                      "message": "Verification code is invalid",
                      "timestamp": "2025-10-22T14:21:10.3171596",
                      "path": "uri=/api/auth/verify"
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "Code invalid",
                                            value = """
                    {
                      "code": "CODE_INVALID",
                      "message": "Verification code is invalid",
                      "timestamp": "2025-10-22T14:21:10.3171596",
                      "path": "uri=/api/auth/verify"
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "Validation fail",
                                            value = """
                    {
                      "code": "INVALID_INPUT",
                      "message": "code: Verification code cannot be blank, userName: Email address cannot be blank",
                      "timestamp": "2025-10-22T14:26:27.6512428",
                      "path": "uri=/api/auth/verify"
                    }
                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - User does not exist",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "NOT_FOUND",
                  "message": "User not found: user@example.com",
                  "timestamp": "2025-10-22T14:20:30",
                  "path": "uri=/api/auth/verify"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - Database failure",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "INTERNAL_ERROR",
                  "message": "An unexpected error occurred",
                  "timestamp": "2025-10-22T13:19:01.4965807",
                  "path": "uri=/api/auth/verify"
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<AuthResponse> verifyEmail(@RequestBody @Valid AuthRequest.Verify request) {
        return ResponseEntity.ok(authService.verifyEmail(request));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "User logout",
            description = "Logout user by revoking the refresh token. After logout, the token cannot be used to refresh access token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful - Refresh token revoked",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    name = "Success - Logout completed",
                                    value = """
                {
                  "message": "Logout successful",
                  "timestamp": "2025-10-22T14:42:15",
                  "path": "uri=/api/auth/logout"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid input format",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "INVALID_INPUT",
                  "message": "refreshToken: Refresh token cannot be blank",
                  "timestamp": "2025-10-22T14:45:41.0104353",
                  "path": "uri=/api/auth/logout"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Token not found or invalid",
                    content =  @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                {
                  "code": "INVALID_TOKEN",
                  "message": "Token is invalid or expired",
                  "timestamp": "2025-10-22T14:42:15",
                  "path": "uri=/api/auth/logout"
                }
                """
                                    ),
                                    @ExampleObject(
                                            value = """
                {
                  "code": "TOKEN_EXPIRED",
                  "message": "Token has expired",
                  "timestamp": "2025-10-22T15:15:12.7877798",
                  "path": "uri=/api/auth/reset-password"
                }
                """
                                )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - Database failure",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "INTERNAL_ERROR",
                  "message": "An unexpected error occurred",
                  "timestamp": "2025-10-22T13:19:01.4965807",
                  "path": "uri=/api/auth/logout"
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<MessageResponse> logout(@RequestBody @Valid AuthRequest.Logout request) {
        return ResponseEntity.ok(authService.logout(request));
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Request password reset",
            description = "Send password reset verification code to user's email. Code expires after a certain time period."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset code sent successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    name = "Success - Code sent",
                                    value = """
                {
                  "message": "Password reset code sent to your email",
                  "timestamp": "2025-10-22T14:51:30",
                  "path": "uri=/api/auth/forgot-password"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - Invalid email format",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "INVALID_INPUT",
                  "message": "emailAddress: Email address must be valid",
                  "timestamp": "2025-10-22T14:56:02.3801642",
                  "path": "uri=/api/auth/forgot-password"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - User does not exist",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "NOT_FOUND",
                  "message": "User not found: user@example.com",
                  "timestamp": "2025-10-22T14:51:30",
                  "path": "uri=/api/auth/forgot-password"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service unavailable - Failed to send email",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "EMAIL_SEND_FAILED",
                  "message": "Failed to send email",
                  "timestamp": "2025-10-22T14:51:30",
                  "path": "uri=/api/auth/forgot-password"
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody @Valid AuthRequest.ForgotPassword request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password with token",
            description = "Reset user password with token sent via email. User must login again after successful reset."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(
                                    name = "Success - Password changed",
                                    value = """
                {
                  "message": "Password reset successful. Please login with your new password",
                  "timestamp": "2025-10-22T14:51:45",
                  "path": "uri=/api/auth/reset-password"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Bad request - Token is invalid or expired",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Code expired",
                                            value = """
                    {
                      "code": "INVALID_TOKEN",
                      "message": "Token is invalid or expired",
                      "timestamp": "2025-10-22T15:06:35.6204868",
                      "path": "uri=/api/auth/reset-password"
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "Weak password",
                                            value = """
                    {
                      "code": "INVALID_OPERATION",
                      "message": "New password must be at least 8 characters long",
                      "timestamp": "2025-10-22T14:51:45",
                      "path": "uri=/api/auth/reset-password"
                    }
                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - User does not exist",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "NOT_FOUND",
                  "message": "User not found: user@example.com",
                  "timestamp": "2025-10-22T14:51:45",
                  "path": "uri=/api/auth/reset-password"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - Database failure",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "INTERNAL_ERROR",
                  "message": "An unexpected error occurred",
                  "timestamp": "2025-10-22T13:19:01.4965807",
                  "path": "uri=/api/auth/logout"
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<MessageResponse> resetPassword(
            @RequestBody @Valid AuthRequest.ResetPassword request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Generate new access token and refresh token pair using valid refresh token. Old refresh token will be revoked after use."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully - Returns new token pair",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Success - New tokens generated",
                                    value = """
                {
                  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQ1VTVE9NRVIiLCJ0eXBlIjoiYWNjZXNzIiwic3ViIjoidXNlckBleGFtcGxlLmNvbSIsImlhdCI6MTc2MTExNzQ5MCwiZXhwIjoxNzYxMTE4MzkwfQ.newAccessTokenSignature",
                  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoicmVmcmVzaCIsInN1YiI6InVzZXJAZXhhbXBsZS5jb20iLCJpYXQiOjE3NjExMTc0OTAsImV4cCI6MTc2MTcyMjI5MH0.newRefreshTokenSignature",
                  "expiresIn": 900,
                  "tokenType": "Bearer",
                  "user": {
                    "id": 5,
                    "email": "user@example.com",
                    "fullName": "Nguyen Van E",
                    "phoneNumber": "0905678901",
                    "role": "CUSTOMER",
                    "status": "ACTIVE",
                    "createdAt": "2025-10-22 14:04:55",
                    "lastLogin": "2025-10-22 14:07:45"
                  },
                  "requiresVerification": false,
                  "message": null
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Token invalid, expired, or revoked",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Token not found",
                                            value = """
                    {
                      "code": "INVALID_TOKEN",
                      "message": "Token is invalid or expired",
                      "timestamp": "2025-10-22T14:31:20",
                      "path": "uri=/api/auth/refresh"
                    }
                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Account is blocked or inactive",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "ACCOUNT_BLOCKED",
                  "message": "Account is blocked",
                  "timestamp": "2025-10-22T14:31:20",
                  "path": "uri=/api/auth/refresh"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - User associated with token does not exist",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "NOT_FOUND",
                  "message": "User not found: user@example.com",
                  "timestamp": "2025-10-22T14:31:20",
                  "path": "uri=/api/auth/refresh"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - Database failure",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "code": "INTERNAL_ERROR",
                  "message": "An unexpected error occurred",
                  "timestamp": "2025-10-22T13:19:01.4965807",
                  "path": "uri=/api/auth/refresh"
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody AuthRequest.RefreshToken request) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}

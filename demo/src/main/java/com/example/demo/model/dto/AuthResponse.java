package com.example.demo.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    @NotBlank(message = "Token cannot be blank")
    private String token;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    @Email(message = "Email address must be valid")
    private String emailAddress;

    @NotNull(message = "Role ID cannot be null")
    private Long roleId;

    @NotBlank(message = "Role name cannot be blank")
    private String roleName;

    @NotNull(message = "isVerified cannot be null")
    private Boolean isVerified;

    @NotNull(message = "requiresVerification cannot be null")
    private Boolean requiresVerification;
}

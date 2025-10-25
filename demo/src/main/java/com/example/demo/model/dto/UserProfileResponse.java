package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.EntityStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class UserProfileResponse {
    // Nested DTO cho user

    @NotNull(message = "User ID cannot be null")
    private Long id;

    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    @NotBlank(message = "Role cannot be blank")
    private String roleDisplayName;

    @NotBlank(message = "Status cannot be blank")
    private EntityStatus status;

    @NotNull(message = "Created at cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime lastLogin;
}
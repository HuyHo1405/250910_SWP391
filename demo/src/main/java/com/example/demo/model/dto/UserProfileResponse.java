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
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String roleDisplayName;
    private EntityStatus status;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime lastLogin;
}
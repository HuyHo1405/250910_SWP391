package com.example.demo.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyRequest {
    @NotBlank(message = "Email address cannot be blank")
    @Email(message = "Email address must be valid")
    private String emailAddress;

    @NotBlank(message = "Verification code cannot be blank")
    private String code;
}

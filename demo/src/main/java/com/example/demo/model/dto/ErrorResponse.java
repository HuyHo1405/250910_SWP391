package com.example.demo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Standard error response")
public class ErrorResponse {
    @Schema(example = "ALREADY_EXISTS")
    private String code;

    @Schema(example = "User already exists with email: user@example.com")
    private String message;

    @Schema(example = "2025-10-22T13:19:01.4965807")
    private LocalDateTime timestamp;

    @Schema(example = "uri=/api/auth/register")
    private String path;
}

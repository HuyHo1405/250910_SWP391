package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianResponse {
    private Long id;
    private String fullName;
    private String emailAddress;
    private String phoneNumber;
    private String role;
    private String status;
}


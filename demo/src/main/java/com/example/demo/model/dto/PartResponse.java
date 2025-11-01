package com.example.demo.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartResponse {
    private Long id;
    private String name;
    private String partNumber;
    private String manufacturer;
    private String description;
    private Double currentUnitPrice;
    private Integer quantity;
    private Integer reserved;
    private String status;           // Enum EntityStatus (ACTIVE, INACTIVE, etc.)
    private LocalDateTime createdAt;
}

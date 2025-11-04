package com.example.demo.model.dto;

import lombok.*;

import java.math.BigDecimal;
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
    private BigDecimal currentUnitPrice;
    private BigDecimal quantity;
    private BigDecimal reserved;
    private String status;           // Enum EntityStatus (ACTIVE, INACTIVE, etc.)
    private LocalDateTime createdAt;
}

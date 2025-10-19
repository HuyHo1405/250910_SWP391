package com.example.demo.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartRequest {

    @NotBlank(message = "Part name is required")
    @Size(max = 255, message = "Part name cannot exceed 255 characters")
    private String name;

    @NotNull(message = "Part number is required")
    @Positive(message = "Part number must be positive")
    private Integer partNumber;

    @NotBlank(message = "Manufacturer is required")
    @Size(max = 255, message = "Manufacturer name cannot exceed 255 characters")
    private String manufacturer;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Unit price must be greater than or equal to 0")
    private Double currentUnitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity;
}

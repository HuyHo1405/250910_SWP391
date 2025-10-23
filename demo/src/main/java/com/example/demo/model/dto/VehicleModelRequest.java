package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.EntityStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;

public class VehicleModelRequest {

    @Data
    public static class CreateModel {
        @NotBlank(message = "Brand name is required")
        @Size(max = 100, message = "Brand name must not exceed 100 characters")
        private String brandName;

        @NotBlank(message = "Model name is required")
        @Size(max = 100, message = "Model name must not exceed 100 characters")
        private String modelName;

        @Size(max = 200, message = "Dimensions must not exceed 200 characters")
        private String dimensions;

        @Pattern(regexp = "^\\d{4}$", message = "Year must be a 4-digit number")
        private String yearIntroduce;

        @Min(value = 1, message = "Seats must be at least 1")
        @Max(value = 50, message = "Seats must not exceed 50")
        private Integer seats;

        @Positive(message = "Battery capacity must be positive")
        @DecimalMax(value = "500.0", message = "Battery capacity must not exceed 500 kWh")
        private Double batteryCapacityKwh;

        @Positive(message = "Range must be positive")
        @DecimalMax(value = "2000.0", message = "Range must not exceed 2000 km")
        private Double rangeKm;

        @Positive(message = "Charging time must be positive")
        @DecimalMax(value = "100.0", message = "Charging time must not exceed 100 hours")
        private Double chargingTimeHours;

        @Positive(message = "Motor power must be positive")
        @DecimalMax(value = "2000.0", message = "Motor power must not exceed 2000 kW")
        private Double motorPowerKw;

        @Positive(message = "Weight must be positive")
        @DecimalMax(value = "10000.0", message = "Weight must not exceed 10000 kg")
        private Double weightKg;

        @JsonIgnore
        private EntityStatus status = EntityStatus.ACTIVE;
    }

    @Data
    public static class UpdateModel {
        @NotBlank(message = "Brand name is required")
        @Size(max = 100, message = "Brand name must not exceed 100 characters")
        private String brandName;

        @NotBlank(message = "Model name is required")
        @Size(max = 100, message = "Model name must not exceed 100 characters")
        private String modelName;

        @Size(max = 200, message = "Dimensions must not exceed 200 characters")
        private String dimensions;

        @Pattern(regexp = "^\\d{4}$", message = "Year must be a 4-digit number")
        private String yearIntroduce;

        @Min(value = 1, message = "Seats must be at least 1")
        @Max(value = 50, message = "Seats must not exceed 50")
        private Integer seats;

        @Positive(message = "Battery capacity must be positive")
        @DecimalMax(value = "500.0", message = "Battery capacity must not exceed 500 kWh")
        private Double batteryCapacityKwh;

        @Positive(message = "Range must be positive")
        @DecimalMax(value = "2000.0", message = "Range must not exceed 2000 km")
        private Double rangeKm;

        @Positive(message = "Charging time must be positive")
        @DecimalMax(value = "100.0", message = "Charging time must not exceed 100 hours")
        private Double chargingTimeHours;

        @Positive(message = "Motor power must be positive")
        @DecimalMax(value = "2000.0", message = "Motor power must not exceed 2000 kW")
        private Double motorPowerKw;

        @Positive(message = "Weight must be positive")
        @DecimalMax(value = "10000.0", message = "Weight must not exceed 10000 kg")
        private Double weightKg;

        @NotNull(message = "Status is required")
        private EntityStatus status;
    }
}

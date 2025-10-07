package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class VehicleRequest {
    @Data
    public static class Create {
        @NotBlank(message = "VIN must not be blank")
        private String vin;                   // Primary Key

        @NotBlank(message = "Vehicle name must not be blank")
        private String name;

        @NotBlank(message = "Plate number must not be blank")
        private String plateNumber;

        @NotBlank(message = "Year must not be blank")
        @Size(min = 4, max = 4, message = "Year must be 4 digits")
        private String year;

        @NotBlank(message = "Color must not be blank")
        private String color;

        @PositiveOrZero(message = "Distance traveled must be >= 0")
        private Double distanceTraveledKm;

        @NotNull(message = "Purchase date must not be null")
        private LocalDateTime purchasedAt;

        @NotNull(message = "User ID must not be null")
        private Long userId;                  // linked User

        @NotNull(message = "Vehicle Model ID must not be null")
        private Long vehicleModelId;          // linked VehicleModel
    }

    @Data
    public static class Update {
        @NotBlank(message = "Vehicle name must not be blank")
        private String name;

        @NotBlank(message = "Plate number must not be blank")
        private String plateNumber;

        @NotBlank(message = "Year must not be blank")
        @Size(min = 4, max = 4, message = "Year must be 4 digits")
        private String year;

        @NotBlank(message = "Color must not be blank")
        private String color;

        @PositiveOrZero(message = "Distance traveled must be >= 0")
        private Double distanceTraveledKm;

        @NotNull(message = "Purchase date must not be null")
        private LocalDateTime purchasedAt;

        @NotNull(message = "Vehicle Model ID must not be null")
        private Long vehicleModelId;
    }

}

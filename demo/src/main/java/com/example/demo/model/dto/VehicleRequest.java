package com.example.demo.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

public class VehicleRequest {
    @Data
    public static class Create {
        @NotBlank(message = "VIN không được để trống")
        private String vin;                   // Primary Key

        @NotBlank(message = "Tên xe không được để trống")
        private String name;

        @NotBlank(message = "Biển số xe không được để trống")
        private String plateNumber;

        @NotBlank(message = "Màu sắc không được để trống")
        private String color;

        @PositiveOrZero(message = "Quãng đường di chuyển phải lớn hơn hoặc bằng 0")
        private Double distanceTraveledKm;

        @Max(100) @Min(0)
        private Double batteryDegradation;

        @NotNull(message = "Ngày mua xe không được để trống")
        private LocalDateTime purchasedAt;

        @NotNull(message = "Mã người dùng không được để trống")
        private Long userId;                  // linked User

        @NotNull(message = "Mã mẫu xe không được để trống")
        private Long vehicleModelId;          // linked VehicleModel
    }

    @Data
    public static class Update {
        @NotBlank(message = "Tên xe không được để trống")
        private String name;

        @NotBlank(message = "Biển số xe không được để trống")
        private String plateNumber;

        @NotBlank(message = "Màu sắc không được để trống")
        private String color;

        @PositiveOrZero(message = "Quãng đường di chuyển phải lớn hơn hoặc bằng 0")
        private Double distanceTraveledKm;

        @Max(100) @Min(0)
        private Double batteryDegradation;

        @NotNull(message = "Ngày mua xe không được để trống")
        private LocalDateTime purchasedAt;

        @NotNull(message = "Mã mẫu xe không được để trống")
        private Long vehicleModelId;
    }

}

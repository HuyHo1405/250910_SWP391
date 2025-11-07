package com.example.demo.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

public class VehicleRequest {
    private static final String PLATE_NUMBER_REGEX =
            "^([0-9]{2}-([A-Z]{1,2}|[A-Z][0-9]) [0-9]{3}\\.?[0-9]{2})|([0-9]{2}[A-Z]-([0-9]{4,5}|[0-9]{3}\\.[0-9]{2}))$";

    // VIN pattern: 17 ký tự, bao gồm chữ cái in hoa và số, không có I, O, Q
    // Ví dụ: VFVF51BC3PA000102
    private static final String VIN_REGEX = "^[A-HJ-NPR-Z0-9]{17}$";

    @Data
    public static class Create {
        @NotBlank(message = "VIN không được để trống")
        @Pattern(regexp = VIN_REGEX, message = "Mã vin không đúng định dạng")
        private String vin;                   // Primary Key

        @NotBlank(message = "Tên xe không được để trống")
        private String name;

        @NotBlank(message = "Biển số xe không được để trống")
        @Pattern(regexp = PLATE_NUMBER_REGEX, message = "Biến số xe không đúng định dạng")
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
        private String name;

        @Pattern(regexp = PLATE_NUMBER_REGEX, message = "Biến số xe không đúng định dạng")
        private String plateNumber;

        private String color;

        @PositiveOrZero(message = "Quãng đường di chuyển phải lớn hơn hoặc bằng 0")
        private Double distanceTraveledKm;

        @Max(100) @Min(0)
        private Double batteryDegradation;

        private LocalDateTime purchasedAt;

    }

}

package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.EntityStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;

public class VehicleModelRequest {

    @Data
    public static class CreateModel {
        @NotBlank(message = "Tên hãng xe không được để trống")
        @Size(max = 100, message = "Tên hãng xe không được vượt quá 100 ký tự")
        private String brandName;

        @NotBlank(message = "Tên mẫu xe không được để trống")
        @Size(max = 100, message = "Tên mẫu xe không được vượt quá 100 ký tự")
        private String modelName;

        @Size(max = 200, message = "Kích thước không được vượt quá 200 ký tự")
        private String dimensions;

        @Min(value = 1, message = "Số ghế phải ít nhất 1")
        @Max(value = 50, message = "Số ghế không được vượt quá 50")
        private Integer seats;

        @Positive(message = "Dung lượng pin phải lớn hơn 0")
        @DecimalMax(value = "500.0", message = "Dung lượng pin không được vượt quá 500 kWh")
        private Double batteryCapacityKwh;

        @Positive(message = "Quãng đường di chuyển phải lớn hơn 0")
        @DecimalMax(value = "2000.0", message = "Quãng đường di chuyển không được vượt quá 2000 km")
        private Double rangeKm;

        @Positive(message = "Thời gian sạc phải lớn hơn 0")
        @DecimalMax(value = "100.0", message = "Thời gian sạc không được vượt quá 100 giờ")
        private Double chargingTimeHours;

        @Positive(message = "Công suất động cơ phải lớn hơn 0")
        @DecimalMax(value = "2000.0", message = "Công suất động cơ không được vượt quá 2000 kW")
        private Double motorPowerKw;

        @Positive(message = "Trọng lượng phải lớn hơn 0")
        @DecimalMax(value = "10000.0", message = "Trọng lượng không được vượt quá 10000 kg")
        private Double weightKg;

        @JsonIgnore
        private EntityStatus status = EntityStatus.ACTIVE;
    }

    @Data
    public static class UpdateModel {
        @NotBlank(message = "Tên hãng xe không được để trống")
        @Size(max = 100, message = "Tên hãng xe không được vượt quá 100 ký tự")
        private String brandName;

        @NotBlank(message = "Tên mẫu xe không được để trống")
        @Size(max = 100, message = "Tên mẫu xe không được vượt quá 100 ký tự")
        private String modelName;

        @Size(max = 200, message = "Kích thước không được vượt quá 200 ký tự")
        private String dimensions;

        @Pattern(regexp = "^\\d{4}$", message = "Năm sản xuất phải là số gồm 4 chữ số")
        private String yearIntroduce;

        @Min(value = 1, message = "Số ghế phải ít nhất 1")
        @Max(value = 50, message = "Số ghế không được vượt quá 50")
        private Integer seats;

        @Positive(message = "Dung lượng pin phải lớn hơn 0")
        @DecimalMax(value = "500.0", message = "Dung lượng pin không được vượt quá 500 kWh")
        private Double batteryCapacityKwh;

        @Positive(message = "Quãng đường di chuyển phải lớn hơn 0")
        @DecimalMax(value = "2000.0", message = "Quãng đường di chuyển không được vượt quá 2000 km")
        private Double rangeKm;

        @Positive(message = "Thời gian sạc phải lớn hơn 0")
        @DecimalMax(value = "100.0", message = "Thời gian sạc không được vượt quá 100 giờ")
        private Double chargingTimeHours;

        @Positive(message = "Công suất động cơ phải lớn hơn 0")
        @DecimalMax(value = "2000.0", message = "Công suất động cơ không được vượt quá 2000 kW")
        private Double motorPowerKw;

        @Positive(message = "Trọng lượng phải lớn hơn 0")
        @DecimalMax(value = "10000.0", message = "Trọng lượng không được vượt quá 10000 kg")
        private Double weightKg;

        @NotNull(message = "Trạng thái không được để trống")
        private EntityStatus status;
    }
}

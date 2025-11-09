package com.example.demo.model.modelEnum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MaintenanceCatalogCategory {
    BATTERY_INSPECTION("Kiểm tra ắc quy"),
    TIRE_ROTATION("Xoay vị trí lốp"),
    BRAKE_SYSTEM_CHECK("Kiểm tra hệ thống phanh"),
    SOFTWARE_UPDATE("Cập nhật phần mềm"),
    AIR_FILTER_REPLACEMENT("Thay lọc gió"),
    COOLANT_SYSTEM_SERVICE("Bảo dưỡng hệ thống làm mát"),
    SUSPENSION_INSPECTION("Kiểm tra hệ thống treo"),
    WHEEL_ALIGNMENT("Cân chỉnh bánh xe"),
    FULL_VEHICLE_DIAGNOSTIC("Chẩn đoán toàn bộ xe"),
    EMERGENCY_CHARGING_SERVICE("Dịch vụ sạc khẩn cấp"),
    BRAKE_FLUID_REPLACEMENT("Thay dầu phanh"),
    AUX_BATTERY_CHECK("Kiểm tra ắc quy phụ"),
    AUX_BATTERY_REPLACEMENT("Thay ắc quy phụ"),
    AC_SYSTEM_SERVICE("Bảo dưỡng hệ thống điều hòa"),
    WIPER_BLADE_REPLACEMENT("Thay cần gạt nước"),
    WASHER_FLUID_TOPUP("Đổ đầy nước rửa kính");

    private final String vietnameseName;

    MaintenanceCatalogCategory(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    // Khi serialize (Response) → trả về tiếng Việt
    @JsonValue
    public String getVietnameseName() {
        return vietnameseName;
    }

    // Khi deserialize (Request) → parse từ tiếng Việt sang enum
    @JsonCreator
    public static MaintenanceCatalogCategory fromVietnameseName(String vietnameseName) {
        if (vietnameseName == null) {
            return null;
        }

        for (MaintenanceCatalogCategory category : values()) {
            if (category.vietnameseName.equals(vietnameseName)) {
                return category;
            }
            // Fallback: nếu client gửi tiếng Anh (BATTERY_INSPECTION)
            if (category.name().equals(vietnameseName)) {
                return category;
            }
        }

        throw new IllegalArgumentException("Không tìm thấy loại dịch vụ: " + vietnameseName);
    }
}

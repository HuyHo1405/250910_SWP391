package com.example.demo.model.modelEnum;

import com.example.demo.exception.CommonException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PartCategory {
    FILTER("Lọc"),
    FLUID("Dung dịch & Hóa chất"),
    BATTERY("Pin & Ắc quy"),
    WIPER("Gạt mưa"),
    TIRE("Lốp xe"),
    BRAKE("Phanh"),
    ELECTRICAL("Điện & Điện tử"),
    SUSPENSION("Hệ thống treo"),
    AC_COMPONENT("Linh kiện điều hòa"),
    OTHER("Khác");

    private final String vietnameseName;

    PartCategory(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    // Khi serialize (Response) → trả về tiếng Việt
    @JsonValue
    public String getVietnameseName() {
        return vietnameseName;
    }

    // Khi deserialize (Request) → parse từ tiếng Việt sang enum
    @JsonCreator
    public static PartCategory fromVietnameseName(String vietnameseName) {
        if (vietnameseName == null) {
            return null;
        }

        for (PartCategory category : values()) {
            if (category.vietnameseName.equals(vietnameseName)) {
                return category;
            }
            // Fallback: nếu client gửi tiếng Anh (FILTER, BATTERY)
            if (category.name().equals(vietnameseName)) {
                return category;
            }
        }

        throw new CommonException.InvalidOperation("Không tìm thấy loại linh kiện: " + vietnameseName);
    }
}

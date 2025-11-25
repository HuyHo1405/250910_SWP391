package com.example.demo.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    private static final String VIN_REGEX = "^[A-HJ-NPR-Z0-9]{17}$";

    @NotNull(message = "Mã người dùng không được để trống")
    @Positive(message = "Mã người dùng phải không âm")
    private Long customerId;

    @NotBlank(message = "Mã VIN không được để trống")
    @Pattern(regexp = VIN_REGEX, message = "Mã vin không đúng định dạng")
    private String vehicleVin;

    @NotNull(message = "Thời gian lịch hẹn không được để trống")
    @Valid
    private ScheduleDateTime scheduleDateTime;

    @Valid
    @NotEmpty(message = "Danh sách dịch vụ không được để trống")
    private List<CatalogDetail> catalogDetails;

    // Method phải bắt đầu với 'is' để validator nhận ra
    @AssertTrue(message = "Các dịch vụ không được trùng nhau")
    private boolean isServiceIdsUnique() {
        if (catalogDetails == null || catalogDetails.isEmpty()) {
            return true; // Skip validation nếu list rỗng (handled by @NotEmpty)
        }

        Set<Long> uniqueIds = new HashSet<>();
        for (CatalogDetail detail : catalogDetails) {
            if (detail.getCatalogId() != null) {
                if (!uniqueIds.add(detail.getCatalogId())) {
                    return false; // Found duplicate
                }
            }
        }
        return true;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CatalogDetail {

        @NotNull(message = "Mã dịch vụ không được trống")
        @Positive(message = "Mã dịch vụ phải không âm")
        private Long catalogId;

//        @NotNull(message = "Mã mẫu xe không được trống")
//        @Positive(message = "Mã mẫu xe phải không âm")
//        private Long modelId;

        @Size(max = 500, message = "Mô tả không vượt quá 500 ký tự")
        private String description;
    }
}

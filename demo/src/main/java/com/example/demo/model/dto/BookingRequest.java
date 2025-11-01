package com.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "Mã người dùng không được để trống")
    @Positive(message = "Mã người dùng phải không âm")
    private Long customerId;

    @NotBlank(message = "Mã VIN không được để trống")
    private String vehicleVin;

    @NotNull(message = "Thời gian lịch hẹn không được để trống")
    @Valid
    private ScheduleDateTime scheduleDateTime;

    @Valid
    private List<ServiceDetail> serviceDetails;

    // Method phải bắt đầu với 'is' để validator nhận ra
    @AssertTrue(message = "Các dịch vụ không được trùng nhau")
    private boolean isServiceIdsUnique() {
        if (serviceDetails == null || serviceDetails.isEmpty()) {
            return true; // Skip validation nếu list rỗng (handled by @NotEmpty)
        }

        Set<Long> uniqueIds = new HashSet<>();
        for (ServiceDetail detail : serviceDetails) {
            if (detail.getServiceId() != null) {
                if (!uniqueIds.add(detail.getServiceId())) {
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
    public static class ServiceDetail {

        @NotNull(message = "Mã dịch vụ không được trống")
        @Positive(message = "Mã dịch vụ phải không âm")
        private Long serviceId;

        @Size(max = 500, message = "Mô tả không vượt quá 500 ký tự")
        private String description;
    }
}

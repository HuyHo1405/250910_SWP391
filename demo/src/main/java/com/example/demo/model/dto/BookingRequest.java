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

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    @NotNull(message = "Vehicle VIN is required")
    private String vehicleVin;

    @NotNull(message = "Schedule date/time is required")
    @Valid
    private ScheduleDateTime scheduleDateTime;

    @NotEmpty(message = "Service list cannot be empty")
    @Valid
    private List<ServiceDetail> serviceDetails;

    // Method phải bắt đầu với 'is' để validator nhận ra
    @AssertTrue(message = "Duplicate service IDs detected in the booking request")
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

        @NotNull(message = "Service ID is required")
        @Positive(message = "Service ID must be positive")
        private Long serviceId;

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        private String description;
    }
}

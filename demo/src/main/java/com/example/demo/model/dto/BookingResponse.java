package com.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// DTO cho response chi tiết booking - trả về cho client
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;

    private Long customerId;
    private String customerName;

    private String vehicleVin;
    private String vehicleModel;

    private ScheduleDateTime scheduleDateTime;

    private String scheduleStatus;     // ENUM: PENDING, CONFIRMED, CHECKED_IN, ...
    private String maintenanceStatus;  // ENUM: IDLE, INSPECTING, IN_PROGRESS, COMPLETE, ...
    private String paymentStatus;      // ENUM: UNPAID, AUTHORIZED, PAID, REFUNDED, ...
    private String lifecycleStatus;    // ENUM: ACTIVE, CANCELLED, COMPLETED, DELIVERED

    private Double totalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<ServiceDetail> serviceDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceDetail {
        private Long id;
        private Long serviceId;
        private String serviceName;
        private String description;
        private Double servicePrice;
    }
}


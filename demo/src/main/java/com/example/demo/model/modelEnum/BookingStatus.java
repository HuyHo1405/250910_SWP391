// Enum tổng hợp thay thế cho BookingLifecycle, MaintenanceStatus, và ScheduleStatus
package com.example.demo.model.modelEnum;

public enum BookingStatus {
    // ===== SCHEDULE PHASE =====
    PENDING,            // Đã đặt lịch, chờ xác nhận
    CONFIRMED,          // Đã xác nhận lịch
    RESCHEDULED,        // Lịch đã được đổi sang giờ khác

    // ===== MAINTENANCE PHASE =====
    IN_PROGRESS,        // Đang sửa/bảo dưỡng
    MAINTENANCE_COMPLETE, // Đã hoàn thiện bảo dưỡng/sửa chữa

    // ===== FINAL PHASE =====
    DELIVERED,          // Xe đã được trả cho khách

    // ===== CANCELLED STATES =====
    CANCELLED,          // Đã hủy trước khi bắt đầu
}

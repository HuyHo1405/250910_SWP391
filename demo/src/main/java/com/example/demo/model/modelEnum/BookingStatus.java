
// Enum tổng hợp thay thế cho BookingLifecycle, MaintenanceStatus, và ScheduleStatus
package com.example.demo.model.modelEnum;

public enum BookingStatus {
    // ===== SCHEDULE PHASE =====
    PENDING,            // Đã đặt lịch, chờ xác nhận
    CONFIRMED,          // Xác nhận (Giả định là khách hàng sẽ luôn đem xe đến)
    PAID,               // Đã thanh toán trước (bắt buộc)

    // ===== MAINTENANCE PHASE =====
    ASSIGNED,           // Đã được phân công kỹ thuật viên
    IN_PROGRESS,        // Đang sửa/bảo dưỡng
    MAINTENANCE_COMPLETE, // Đã hoàn thiện bảo dưỡng/sửa chữa

    // ===== CANCELLED STATES =====
    CANCELLED,          // Đã hủy trước khi bắt đầu
    REJECTED,           // Bị từ chối (nếu có lý do hợp lệ)
}

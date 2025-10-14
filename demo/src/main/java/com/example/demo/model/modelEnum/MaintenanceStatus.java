// Maintenance: trạng thái bảo dưỡng/sửa chữa xe
package com.example.demo.model.modelEnum;

public enum MaintenanceStatus {
    IDLE,               // Chưa bắt đầu kiểm tra
    INSPECTING,         // Đang kiểm tra xe
    WAITING_APPROVAL,   // Đã báo giá, chờ admin duyệt
    IN_PROGRESS,        // Đang sửa/bảo dưỡng
    COMPLETE,           // Đã hoàn thiện bảo dưỡng/sửa chữa
    ABORTED             // Đơn bị huỷ khi đang làm dở (do lỗi hoặc cancel)
}

// Booking tổng chỉ lưu trạng thái lifecycle (đặt, hoàn tất, giao xe, huỷ)
package com.example.demo.model.modelEnum;

public enum BookingLifecycle {
    ACTIVE,       // Đơn đang hoạt động (chưa hoàn tất/hủy)
    CANCELLED,    // Đã huỷ (bất cứ lý do nào)
    COMPLETED,    // Đã hoàn tất bảo dưỡng và giao xe
    DELIVERED     // Xe đã được trả cho khách
}

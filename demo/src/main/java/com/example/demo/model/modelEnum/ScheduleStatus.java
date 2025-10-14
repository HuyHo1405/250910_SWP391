// Trạng thái scheduling booking (lịch hẹn, xác nhận, checkin,...)
package com.example.demo.model.modelEnum;

public enum ScheduleStatus {
    PENDING,      // Đã đặt lịch, chờ xác nhận
    CONFIRMED,    // Đã xác nhận lịch
    CHECKED_IN,   // Khách đã đến garage/cửa hàng
    NO_SHOW,      // Khách không đến đúng giờ
    RESCHEDULED,   // Lịch đã được đổi sang giờ khác
    CANCELLED
}

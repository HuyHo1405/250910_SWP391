// Payment: trạng thái thanh toán, tiền
package com.example.demo.model.modelEnum;

public enum PaymentStatus {
    UNPAID,      // Chưa thanh toán
    AUTHORIZED,  // Đã pre-auth (khóa tiền/đặt cọc)
    PAID,        // Đã thanh toán xong
    REFUNDED,    // Đã hoàn tiền cho khách
    FAILED,      // Thanh toán lỗi
    VOIDED       // Thanh toán bị void (khi huỷ đơn khi chưa capture)
}

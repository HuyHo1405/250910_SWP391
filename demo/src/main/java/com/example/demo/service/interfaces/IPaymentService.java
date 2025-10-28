package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingResponse;

public interface IPaymentService {
    BookingResponse pay(Long bookingId);                              // chuyển AUTHORIZED → PAID
    BookingResponse refund(Long bookingId, String reason);            // chuyển PAID → REFUNDED
    BookingResponse cancelPayment(Long bookingId, String reason);     // huỷ thanh toán
}

package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingResponse;

public interface IPaymentService {
    BookingResponse authorize(Long bookingId, Double amount);         // chuyển UNPAID → AUTHORIZED
    BookingResponse pay(Long bookingId);                              // chuyển AUTHORIZED → PAID
    BookingResponse refund(Long bookingId, String reason);            // chuyển PAID → REFUNDED
    BookingResponse voidPayment(Long bookingId);                      // huỷ trước khi pay/capture
    BookingResponse cancelPayment(Long bookingId, String reason);     // huỷ thanh toán
}

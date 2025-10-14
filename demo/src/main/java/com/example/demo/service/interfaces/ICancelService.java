package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingResponse;

public interface ICancelService {
    BookingResponse cancelAll(Long bookingId, String reason);
}

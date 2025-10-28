package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingResponse;

public interface IBookingStatusService {
    BookingResponse confirmBooking(Long id);
    BookingResponse startMaintenance(Long id);
    BookingResponse completeMaintenance(Long id);
    BookingResponse cancelBooking(Long id, String reason);
}

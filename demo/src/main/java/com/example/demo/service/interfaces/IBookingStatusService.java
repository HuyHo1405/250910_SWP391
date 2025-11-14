package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingResponse;

public interface IBookingStatusService {
     boolean checkEnoughPartForBooking(Long bookingId);
    BookingResponse confirmBooking(Long id);
    BookingResponse startMaintenance(Long id);
    BookingResponse completeMaintenance(Long id);
    BookingResponse cancelBooking(Long id, String reason);
    BookingResponse rejectBooking(Long id, String reason);
}

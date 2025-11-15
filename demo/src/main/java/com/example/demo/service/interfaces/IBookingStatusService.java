package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingResponse;

public interface IBookingStatusService {
     boolean checkEnoughPartForBooking(Long bookingId);
    BookingResponse confirmBooking(Long id);
    BookingResponse startMaintenance(Long id, Long TechnicianId);
    BookingResponse cancelBooking(Long id);
    BookingResponse rejectBooking(Long id);
}

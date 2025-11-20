package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingResponse;

public interface IBookingStatusService {
     boolean checkEnoughPartForBooking(Long bookingId);
    BookingResponse confirmBooking(Long id);
    BookingResponse assignTechnician(Long id, Long technicianId);
    BookingResponse reassignTechnician(Long id, Long technicianId, String reason);
    BookingResponse cancelBooking(Long id);
    BookingResponse rejectBooking(Long id);
}

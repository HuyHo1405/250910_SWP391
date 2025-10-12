package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingResponse;

public interface IBookingStatusService {
    BookingResponse confirm(Long bookingId, String reason);

    BookingResponse checkin(Long bookingId, String reason);

    BookingResponse inspect(Long bookingId, String reason);

    BookingResponse waitForApproval(Long bookingId, String reason);

    BookingResponse startService(Long bookingId, String reason);

    BookingResponse complete(Long bookingId, String reason);

    BookingResponse delivered(Long bookingId, String reason);

    BookingResponse cancel(Long bookingId, String reason);
}

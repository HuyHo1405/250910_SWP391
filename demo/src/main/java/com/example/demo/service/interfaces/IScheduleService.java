package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.dto.ScheduleDateTime;

public interface IScheduleService {
    BookingResponse confirm(Long bookingId);             // chuyển PENDING → CONFIRMED
    BookingResponse checkIn(Long bookingId);             // chuyển CONFIRMED → CHECKED_IN
    BookingResponse reschedule(Long bookingId, ScheduleDateTime newTime); // đổi lịch
    BookingResponse noShow(Long bookingId);              // đánh dấu khách no-show
    BookingResponse cancelSchedule(Long bookingId, String reason); // huỷ lịch (release slot)
}

package com.example.demo.service.impl;

import com.example.demo.exception.BookingException; // Import mới
import com.example.demo.exception.CommonException;  // Import mới
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.dto.ScheduleDateTime;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.modelEnum.ScheduleStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.service.interfaces.IScheduleService;
import com.example.demo.utils.BookingResponseMapper;
import com.example.demo.utils.ScheduleDateTimeParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleService implements IScheduleService {

    private final AccessControlService accessControlService;
    private final BookingRepo bookingRepo;

    private Booking findBookingAndVerifyAccess(Long bookingId, String action) {
        Booking booking = bookingRepo.findById(bookingId)
                // ✅ Sửa: Dùng NotFound để rõ ràng hơn về việc không tìm thấy tài nguyên
                .orElseThrow(() -> new CommonException.NotFound("Booking", bookingId));

        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "SCHEDULE", action);

        if (booking.getScheduleStatus() == ScheduleStatus.CANCELLED) {
            // ✅ Sửa: Dùng InvalidStatusTransition để mô tả lỗi nghiệp vụ cụ thể
            throw new BookingException.InvalidStatusTransition(
                    "Schedule",
                    ScheduleStatus.CANCELLED.name(),
                    action
            );
        }

        return booking;
    }

    @Transactional
    public BookingResponse updateScheduleStatus(Booking booking, ScheduleStatus status, ScheduleDateTime newTime, String reason) {
        booking.setScheduleStatus(status);
        if (newTime != null) {
            booking.setScheduleDate(ScheduleDateTimeParser.parse(newTime));
        }
        bookingRepo.save(booking);
        return BookingResponseMapper.toDto(booking);
    }

    // --- Các phương thức public không cần thay đổi ---

    @Transactional
    @Override
    public BookingResponse confirm(Long bookingId) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "confirm");
        return updateScheduleStatus(booking, ScheduleStatus.CONFIRMED, null, null);
    }

    @Transactional
    @Override
    public BookingResponse checkIn(Long bookingId) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "checkin");
        return updateScheduleStatus(booking, ScheduleStatus.CHECKED_IN, null, null);
    }

    @Transactional
    @Override
    public BookingResponse reschedule(Long bookingId, ScheduleDateTime newTime) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "reschedule");
        return updateScheduleStatus(booking, ScheduleStatus.RESCHEDULED, newTime, null);
    }

    @Transactional
    @Override
    public BookingResponse noShow(Long bookingId) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "no-show");
        return updateScheduleStatus(booking, ScheduleStatus.NO_SHOW, null, null);
    }

    @Transactional
    @Override
    public BookingResponse cancelSchedule(Long bookingId, String reason) {
        Booking booking = findBookingAndVerifyAccess(bookingId, "cancel");
        return updateScheduleStatus(booking, ScheduleStatus.CANCELLED, null, reason);
    }
}
package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.service.interfaces.IBookingStatusService;
import com.example.demo.utils.BookingResponseMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingStatusService implements IBookingStatusService {
    private final BookingRepo bookingRepository;

    // State chart: allowed transitions
    private static final Map<BookingStatus, Set<BookingStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(BookingStatus.class);
    static {
        ALLOWED_TRANSITIONS.put(BookingStatus.PENDING, EnumSet.of(BookingStatus.CONFIRMED, BookingStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(BookingStatus.CONFIRMED, EnumSet.of(BookingStatus.CHECKIN, BookingStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(BookingStatus.CHECKIN, EnumSet.of(BookingStatus.INSPECTING, BookingStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(BookingStatus.INSPECTING, EnumSet.of(BookingStatus.WAITING_APPROVAL));
        ALLOWED_TRANSITIONS.put(BookingStatus.WAITING_APPROVAL, EnumSet.of(BookingStatus.IN_PROGRESS, BookingStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(BookingStatus.IN_PROGRESS, EnumSet.of(BookingStatus.COMPLETED));
        ALLOWED_TRANSITIONS.put(BookingStatus.COMPLETED, EnumSet.of(BookingStatus.DELIVERED));
        // DELIVERED, CANCELLED: kết thúc, không chuyển tiếp
    }

    private Booking transition(Long bookingId, BookingStatus nextStatus, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Booking", bookingId));
        BookingStatus currentStatus = booking.getStatus();

        if (!isAllowedTransition(currentStatus, nextStatus)) {
            throw new CommonException.InvalidOperation(
                    "Transition from " + currentStatus + " to " + nextStatus + " is not allowed."
            );
        }

        booking.setStatus(nextStatus);
        // Audit/log nếu cần: booking.logStatusTransition(...)
        booking = bookingRepository.save(booking);
        return booking;
    }

    private boolean isAllowedTransition(BookingStatus current, BookingStatus next) {
        Set<BookingStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, EnumSet.noneOf(BookingStatus.class));
        return allowed.contains(next);
    }

    @Override
    public BookingResponse confirm(Long bookingId, String reason) {
        return BookingResponseMapper.toDto(
                transition(bookingId, BookingStatus.CONFIRMED, reason)
        );
    }

    @Override
    public BookingResponse checkin(Long bookingId, String reason) {
        return BookingResponseMapper.toDto(
                transition(bookingId, BookingStatus.CHECKIN, reason)
        );
    }

    @Override
    public BookingResponse inspect(Long bookingId, String reason) {
        return BookingResponseMapper.toDto(
                transition(bookingId, BookingStatus.INSPECTING, reason)
        );
    }

    @Override
    public BookingResponse waitForApproval(Long bookingId, String reason) {
        return BookingResponseMapper.toDto(
                transition(bookingId, BookingStatus.WAITING_APPROVAL, reason)
        );
    }

    @Override
    public BookingResponse startService(Long bookingId, String reason) {
        return BookingResponseMapper.toDto(
                transition(bookingId, BookingStatus.IN_PROGRESS, reason)
        );
    }

    @Override
    public BookingResponse complete(Long bookingId, String reason) {
        return BookingResponseMapper.toDto(
                transition(bookingId, BookingStatus.COMPLETED, reason)
        );
    }

    @Override
    public BookingResponse delivered(Long bookingId, String reason) {
        return BookingResponseMapper.toDto(
                transition(bookingId, BookingStatus.DELIVERED, reason)
        );
    }

    @Override
    public BookingResponse cancel(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CommonException.NotFound("Booking", bookingId));
        BookingStatus currentStatus = booking.getStatus();
        if (!EnumSet.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.CHECKIN, BookingStatus.WAITING_APPROVAL).contains(currentStatus)) {
            throw new CommonException.InvalidOperation(
                    "Cannot cancel booking in status " + currentStatus
            );
        }
        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);
        return BookingResponseMapper.toDto(booking);
    }
}

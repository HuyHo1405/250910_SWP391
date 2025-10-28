package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.dto.InvoiceRequest;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.Invoice;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.model.modelEnum.InvoiceStatus;
import com.example.demo.model.modelEnum.PaymentStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.repo.InvoiceRepo;
import com.example.demo.service.interfaces.IBookingStatusService;
import com.example.demo.service.interfaces.IInvoiceService;
import com.example.demo.utils.BookingResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingStatusService implements IBookingStatusService {

    private final IInvoiceService invoiceService;
    private final AccessControlService accessControlService;

    private final BookingRepo bookingRepository;

    // Các trạng thái cho phép cancel
    private final List<BookingStatus> CANCELLABLE_STATUSES = Arrays.asList(
            BookingStatus.PENDING
    );
    private final InvoiceRepo invoiceRepo;

    @Override
    @Transactional
    public BookingResponse confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        accessControlService.verifyCanAccessAllResources( "BOOKING", "confirm");

        //TODO check số lượng linh kiện? confirm: cancel

        //TODO chỉnh dô reserved trong part

        // Chỉ cho phép xác nhận nếu đang ở trạng thái PENDING
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new CommonException.InvalidOperation("Only PENDING bookings can be confirmed.");
        }

        // Chuyển trạng thái sang CONFIRMED
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        invoiceService.create(id);
        bookingRepository.save(booking);

        return BookingResponseMapper.toDto(booking);
    }


    @Override
    public BookingResponse startMaintenance(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        accessControlService.verifyCanAccessAllResources( "BOOKING", "start-maintenance");

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new CommonException.InvalidOperation("Booking is not confirmed for this operation!");
        }

        booking.setBookingStatus(BookingStatus.IN_PROGRESS);

        //TODO update part: quantity = current - reserved

        return BookingResponseMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long id, String reason) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "BOOKING", "cancel");

        // Kiểm tra trạng thái đã bị hủy chưa
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new CommonException.InvalidOperation("Booking is already cancelled");
        }

        // Kiểm tra trạng thái có cho phép cancel không
        if (!CANCELLABLE_STATUSES.contains(booking.getBookingStatus())) {
            throw new CommonException.InvalidOperation(
                    "Cannot cancel booking in status: " + booking.getBookingStatus() +
                            ". Only PENDING bookings can be cancelled."
            );
        }

        // Cập nhật trạng thái thành CANCELLED
        booking.setBookingStatus(BookingStatus.CANCELLED);

        log.info("Booking {} cancelled. Reason: {}", id, reason);
        return BookingResponseMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse completeMaintenance(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));
        accessControlService.verifyCanAccessAllResources("BOOKING", "complete");

        // Kiểm tra trạng thái trước khi complete
        if (booking.getBookingStatus() != BookingStatus.IN_PROGRESS) {
            throw new CommonException.InvalidOperation(
                    "Cannot complete booking that has not in progress."
            );
        }

        // Chuyển sang trạng thái hoàn thành
        booking.setBookingStatus(BookingStatus.MAINTENANCE_COMPLETE);
        invoiceService.updateStatus(id, InvoiceStatus.UNPAID);

        log.info("Booking {} marked as delivered/completed", id);
        return BookingResponseMapper.toDto(bookingRepository.save(booking));
    }
}

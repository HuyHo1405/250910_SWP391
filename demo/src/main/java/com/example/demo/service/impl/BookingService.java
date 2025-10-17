package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.dto.ScheduleDateTime;
import com.example.demo.model.entity.*;
import com.example.demo.model.modelEnum.*;
import com.example.demo.repo.BookingRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.repo.VehicleRepo;
import com.example.demo.service.interfaces.IBookingDetailService;
import com.example.demo.service.interfaces.IBookingService;
import com.example.demo.utils.ScheduleDateTimeParser;
import com.example.demo.utils.BookingResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService implements IBookingService {

    private final IBookingDetailService bookingDetailService;
    private final AccessControlService accessControlService;
    private final BookingRepo bookingRepository;
    private final UserRepo userRepository;
    private final VehicleRepo vehicleRepository;

    // Các trạng thái cho phép cancel
    private final List<BookingStatus> CANCELLABLE_STATUSES = Arrays.asList(
            BookingStatus.PENDING,
            BookingStatus.CONFIRMED,
            BookingStatus.RESCHEDULED
    );

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for customer {}, vehicle {}", request.getCustomerId(), request.getVehicleVin());

        accessControlService.verifyResourceAccess(request.getCustomerId(), "BOOKING", "CREATE");

        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CommonException.NotFound("User", request.getCustomerId()));
        Vehicle vehicle = vehicleRepository.findByVin(request.getVehicleVin())
                .orElseThrow(() -> new CommonException.NotFound("Vehicle", request.getVehicleVin()));

        accessControlService.verifyResourceAccess(vehicle.getUser().getId(), "VEHICLE", "READ");

        LocalDateTime scheduleDate = checkFutureScheduleDate(request.getScheduleDateTime());

        Booking booking = Booking.builder()
                .customer(customer)
                .vehicle(vehicle)
                .scheduleDate(scheduleDate)
                .bookingStatus(BookingStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .build();
        booking = bookingRepository.save(booking);

        if (request.getServiceDetails() != null) {
            for (BookingRequest.ServiceDetail serviceDetail : request.getServiceDetails()) {
                bookingDetailService.addServiceToBooking(booking.getId(), serviceDetail);
            }
        }

        double totalPrice = bookingDetailService.calculateBookingTotal(booking.getId());
        booking.setTotalPrice(totalPrice);
        booking = bookingRepository.save(booking);

        return BookingResponseMapper.toDto(booking, request.getScheduleDateTime());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));
        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "BOOKING", "read");
        return BookingResponseMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomerId(Long customerId) {
        if (!userRepository.existsById(customerId))
            throw new CommonException.NotFound("User", customerId);
        accessControlService.verifyResourceAccess(customerId, "BOOKING", "read");
        return bookingRepository.findByCustomerId(customerId)
                .stream().map(BookingResponseMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByVehicleVin(String vin) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
                .orElseThrow(() -> new CommonException.NotFound("Vehicle", vin));
        accessControlService.verifyResourceAccess(vehicle.getUser().getId(), "BOOKING", "read");
        return bookingRepository.findByVehicleVin(vin)
                .stream().map(BookingResponseMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookingsFiltered(
            BookingStatus bookingStatus,
            PaymentStatus paymentStatus) {

        accessControlService.verifyCanAccessAllResources("BOOKING", "read");

        List<Booking> bookings = bookingRepository.findWithFilters(bookingStatus, paymentStatus);

        return bookings.stream()
                .map(BookingResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse updateBooking(Long id, BookingRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "BOOKING", "update");

        // Không cho update nếu đã hoàn thành hoặc đã hủy
        if (booking.getBookingStatus() == BookingStatus.DELIVERED
                || booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new CommonException.InvalidOperation("Cannot update booking in status: " + booking.getBookingStatus());
        }

        if (request.getScheduleDateTime() != null) {
            LocalDateTime scheduleDate = checkFutureScheduleDate(request.getScheduleDateTime());
            booking.setScheduleDate(scheduleDate);
            // Nếu đổi lịch, có thể đổi status sang RESCHEDULED
            if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                booking.setBookingStatus(BookingStatus.RESCHEDULED);
            }
        }

        if (request.getVehicleVin() != null) {
            Vehicle vehicle = vehicleRepository.findByVin(request.getVehicleVin())
                    .orElseThrow(() -> new CommonException.NotFound("Vehicle", request.getVehicleVin()));
            booking.setVehicle(vehicle);
        }

        if (request.getServiceDetails() != null) {
            bookingDetailService.updateBookingServices(id, request.getServiceDetails());
            double totalPrice = bookingDetailService.calculateBookingTotal(id);
            booking.setTotalPrice(totalPrice);
        }

        booking = bookingRepository.save(booking);

        ScheduleDateTime responseSchedule = request.getScheduleDateTime() != null
                ? request.getScheduleDateTime()
                : ScheduleDateTimeParser.format(booking.getScheduleDate(), "yyyy-MM-dd HH:mm:ss", "Asia/Ho_Chi_Minh");
        return BookingResponseMapper.toDto(booking, responseSchedule);
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        // Không cho xóa nếu đã hoàn thành hoặc hủy
        if (booking.getBookingStatus() == BookingStatus.DELIVERED
                || booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new CommonException.InvalidOperation("Cannot delete booking in status: " + booking.getBookingStatus());
        }

        bookingRepository.delete(booking);
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
                            ". Only PENDING, CONFIRMED, or RESCHEDULED bookings can be cancelled."
            );
        }

        // Kiểm tra xem đã thanh toán chưa - nếu đã thanh toán thì cần hoàn tiền
        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            // TODO: Tích hợp payment service để xử lý refund
            booking.setPaymentStatus(PaymentStatus.REFUNDED);
            log.info("Payment refund initiated for booking {}", id);
        }

        // Cập nhật trạng thái thành CANCELLED
        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);

        log.info("Booking {} cancelled. Reason: {}", id, reason);
        return BookingResponseMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingResponse completeBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));
        accessControlService.verifyCanAccessAllResources("BOOKING", "complete");

        // Kiểm tra trạng thái trước khi complete
        if (booking.getBookingStatus() != BookingStatus.MAINTENANCE_COMPLETE) {
            throw new CommonException.InvalidOperation(
                    "Cannot complete booking in status: " + booking.getBookingStatus() +
                            ". Booking must be in MAINTENANCE_COMPLETE status first."
            );
        }

        // Kiểm tra đã thanh toán chưa
        if (booking.getPaymentStatus() != PaymentStatus.PAID) {
            throw new CommonException.InvalidOperation(
                    "Cannot complete booking with unpaid status. Please complete payment first."
            );
        }

        // Chuyển sang trạng thái hoàn thành - DELIVERED
        booking.setBookingStatus(BookingStatus.DELIVERED);
        booking = bookingRepository.save(booking);

        log.info("Booking {} marked as delivered/completed", id);
        return BookingResponseMapper.toDto(booking);
    }

    private LocalDateTime checkFutureScheduleDate(ScheduleDateTime scheduleDate) {
        LocalDateTime bookingDate = ScheduleDateTimeParser.parse(scheduleDate);
        if (bookingDate.isBefore(LocalDateTime.now())) {
            throw new CommonException.InvalidOperation("Booking date/time must be in the future.");
        }
        return bookingDate;
    }
}

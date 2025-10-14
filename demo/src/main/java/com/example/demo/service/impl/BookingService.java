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
import com.example.demo.service.interfaces.ICancelService;
import com.example.demo.utils.ScheduleDateTimeParser;
import com.example.demo.utils.BookingResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService implements IBookingService {

    private final IBookingDetailService bookingDetailService;
    private final AccessControlService accessControlService;
    private final ICancelService cancelService;
    private final BookingRepo bookingRepository;
    private final UserRepo userRepository;
    private final VehicleRepo vehicleRepository;

    // Tạo mới booking với các status KHỞI TẠO mặc định từng domain
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
                .scheduleStatus(ScheduleStatus.PENDING)
                .maintenanceStatus(MaintenanceStatus.IDLE)
                .paymentStatus(PaymentStatus.UNPAID)
                .lifecycleStatus(BookingLifecycle.ACTIVE)
                .build();
        booking = bookingRepository.save(booking);

        for (BookingRequest.ServiceDetail serviceDetail : request.getServiceDetails()) {
            bookingDetailService.addServiceToBooking(booking.getId(), serviceDetail);
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
        Vehicle vehicle = vehicleRepository.findByVinAndEntityStatus(vin, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CommonException.NotFound("Vehicle", vin));
        accessControlService.verifyResourceAccess(vehicle.getUser().getId(), "BOOKING", "read");
        return bookingRepository.findByVehicleVin(vin)
                .stream().map(BookingResponseMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookingsFiltered(
            BookingLifecycle lifecycleStatus,
            ScheduleStatus scheduleStatus,
            MaintenanceStatus maintenanceStatus,
            PaymentStatus paymentStatus) {

        // Luôn kiểm tra quyền trước khi thực hiện
        accessControlService.verifyCanAccessAllResources("BOOKING", "read");

        List<Booking> bookings = bookingRepository.findWithFilters(
                lifecycleStatus, scheduleStatus, maintenanceStatus, paymentStatus
        );

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


        if (booking.getLifecycleStatus() == BookingLifecycle.COMPLETED
                || booking.getLifecycleStatus() == BookingLifecycle.CANCELLED)
            throw new CommonException.InvalidOperation("Cannot update booking in lifecycle status: " + booking.getLifecycleStatus());

        if (request.getScheduleDateTime() != null) {
            LocalDateTime scheduleDate = checkFutureScheduleDate(request.getScheduleDateTime());
            booking.setScheduleDate(scheduleDate);
        }

        if (request.getVehicleVin() != null) {
            Vehicle vehicle = vehicleRepository.findByVin(request.getVehicleVin())
                    .orElseThrow(() -> new CommonException.NotFound("Vehicle", request.getVehicleVin()));
            booking.setVehicle(vehicle);
        }

        bookingDetailService.updateBookingServices(id, request.getServiceDetails());
        double totalPrice = bookingDetailService.calculateBookingTotal(id);
        booking.setTotalPrice(totalPrice);

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
        if (booking.getLifecycleStatus() == BookingLifecycle.COMPLETED
                || booking.getLifecycleStatus() == BookingLifecycle.CANCELLED)
            throw new CommonException.InvalidOperation("Cannot delete booking in status: " + booking.getLifecycleStatus());
        bookingRepository.delete(booking);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long id, String reason) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));
        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "BOOKING", "cancel");

        return cancelService.cancelAll(id, reason);
    }

    @Override
    @Transactional
    public BookingResponse completeBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));
        accessControlService.verifyCanAccessAllResources("BOOKING", "complete");
        booking.setLifecycleStatus(BookingLifecycle.COMPLETED);
        booking = bookingRepository.save(booking);
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

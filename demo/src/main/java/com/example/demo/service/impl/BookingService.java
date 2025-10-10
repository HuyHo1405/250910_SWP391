package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.entity.*;
import com.example.demo.repo.BookingRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.repo.VehicleRepo;
import com.example.demo.service.interfaces.IBookingDetailService;
import com.example.demo.service.interfaces.IBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for customer {}, vehicle {}", request.getCustomerId(), request.getVehicleVin());

        // Kiểm tra quyền CREATE booking gồm cả permission + ownership (nếu cần)
        accessControlService.verifyResourceAccess(request.getCustomerId(), "BOOKING", "create");

        // Validate customer
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CommonException.NotFound("User", request.getCustomerId()));
        // Validate vehicle
        Vehicle vehicle = vehicleRepository.findByVin(request.getVehicleVin())
                .orElseThrow(() -> new CommonException.NotFound("Vehicle", request.getVehicleVin()));

        // Check quyền với xe cũng y hệt (nếu theo business cần)
        accessControlService.verifyResourceAccess(vehicle.getUser().getId(), "VEHICLE", "create");

        // Từ đây business tạo booking như cũ...
        Booking booking = Booking.builder()
                .customer(customer)
                .vehicle(vehicle)
                .scheduleDate(request.getScheduleDate())
                .status(BookingStatus.PENDING)
                .build();
        booking = bookingRepository.save(booking);

        for (BookingRequest.ServiceDetail serviceDetail : request.getServiceDetails()) {
            bookingDetailService.addServiceToBooking(booking.getId(), serviceDetail);
        }

        double totalPrice = bookingDetailService.calculateBookingTotal(booking.getId());
        booking.setTotalPrice(totalPrice);
        booking = bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));
        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "BOOKING", "read");
        return mapToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomerId(Long customerId) {
        if (!userRepository.existsById(customerId))
            throw new CommonException.NotFound("User", customerId);
        accessControlService.verifyResourceAccess(customerId, "BOOKING", "read");
        return bookingRepository.findByCustomerId(customerId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByVehicleVin(String vin) {
        Vehicle vehicle = vehicleRepository.findByVinAndEntityStatus(vin, EntityStatus.ACTIVE)
                .orElseThrow(() -> new CommonException.NotFound("Vehicle", vin));


        accessControlService.verifyResourceAccess(vehicle.getUser().getId(), "BOOKING", "read");
        return bookingRepository.findByVehicleVin(vin)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public BookingResponse updateBooking(Long id, BookingRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CANCELLED)
            throw new CommonException.InvalidOperation("Cannot update booking in status: " + booking.getStatus());

        accessControlService.verifyResourceAccessWithoutOwnership("BOOKING", "update");
        if (request.getScheduleDate() != null)
            booking.setScheduleDate(request.getScheduleDate());

        if (request.getVehicleVin() != null) {
            Vehicle vehicle = vehicleRepository.findByVin(request.getVehicleVin())
                    .orElseThrow(() -> new CommonException.NotFound("Vehicle", request.getVehicleVin()));
            booking.setVehicle(vehicle);
        }

        bookingDetailService.updateBookingServices(id, request.getServiceDetails());
        double totalPrice = bookingDetailService.calculateBookingTotal(id);
        booking.setTotalPrice(totalPrice);

        booking = bookingRepository.save(booking);
        return mapToResponse(booking);
    }

    @Override
    public BookingResponse updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));
        accessControlService.verifyResourceAccessWithoutOwnership("BOOKING", "update");
        booking.setStatus(status);
        booking = bookingRepository.save(booking);
        return mapToResponse(booking);
    }

    @Override
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));
        if (booking.getStatus() == BookingStatus.COMPLETED)
            throw new CommonException.InvalidOperation("Cannot cancel completed booking");

        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "BOOKING", "cancel");
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateTotalPrice(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        // Truyền ownerId vào access control service để chỉ chủ, staff, admin xem được giá đơn này
        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "BOOKING", "read");

        return bookingDetailService.calculateBookingTotal(id);
    }

    private BookingResponse mapToResponse(Booking booking) {
        List<BookingResponse.ServiceDetail> serviceDetails = booking.getBookingDetails().stream()
                .map(detail -> BookingResponse.ServiceDetail.builder()
                        .id(detail.getId())
                        .serviceId(detail.getService().getId())
                        .serviceName(detail.getService().getName())
                        .description(detail.getDescription())
                        .servicePrice(detail.getServicePrice())
                        .build())
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer().getId())
                .customerName(booking.getCustomer().getFullName())
                .vehicleVin(booking.getVehicle().getVin())
                .vehicleModel(booking.getVehicle().getModel().getModelName())
                .scheduleDate(booking.getScheduleDate())
                .status(booking.getStatus().name())
                .totalPrice(booking.getTotalPrice())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .serviceDetails(serviceDetails)
                .build();
    }
}


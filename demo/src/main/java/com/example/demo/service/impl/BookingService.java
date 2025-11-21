package com.example.demo.service.impl;

import com.example.demo.exception.BookingException;
import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.dto.ScheduleDateTime;
import com.example.demo.model.entity.*;
import com.example.demo.model.modelEnum.*;
import com.example.demo.repo.BookingRepo;
import com.example.demo.repo.MaintenanceCatalogRepo;
import com.example.demo.repo.UserRepo;
import com.example.demo.repo.VehicleRepo;
import com.example.demo.service.interfaces.IBookingDetailService;
import com.example.demo.service.interfaces.IBookingService;
import com.example.demo.service.interfaces.IInvoiceService;
import com.example.demo.utils.ScheduleDateTimeParser; // Giữ nguyên util parser
import com.example.demo.utils.BookingResponseMapper; // <-- THAY ĐỔI IMPORT
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class
BookingService implements IBookingService {

    private final IBookingDetailService bookingDetailService;
    private final IInvoiceService invoiceService;
    private final AccessControlService accessControlService;

    private final BookingRepo bookingRepository;
    private final UserRepo userRepository;
    private final VehicleRepo vehicleRepository;
    private final MaintenanceCatalogRepo maintenanceCatalogRepo;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for customer {}, vehicle {}", request.getCustomerId(), request.getVehicleVin());

        accessControlService.verifyResourceAccess(request.getCustomerId(), "BOOKING", "CREATE");

        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CommonException.NotFound("User", request.getCustomerId()));
        Vehicle vehicle = vehicleRepository.findByVin(request.getVehicleVin())
                .orElseThrow(() -> new CommonException.NotFound("Vehicle", request.getVehicleVin()));

        accessControlService.verifyResourceAccess(vehicle.getCustomer().getId(), "VEHICLE", "READ");

        LocalDateTime scheduleDate = checkFutureScheduleDate(request.getScheduleDateTime());

        checkAvailableSlot(scheduleDate);

        checkBookingSingleDayLimit(customer.getId(), scheduleDate.toLocalDate());

        Booking booking = Booking.builder()
                .customer(customer)
                .vehicle(vehicle)
                .scheduleDate(scheduleDate)
                .bookingStatus(BookingStatus.PENDING)
                .build();
        booking = bookingRepository.save(booking);

        if (request.getCatalogDetails() != null) {
            for (BookingRequest.CatalogDetail serviceDetail : request.getCatalogDetails()) {
                if(!maintenanceCatalogRepo.isServiceValidForVin(serviceDetail.getCatalogId(), vehicle.getVin())) {
                    throw new BookingException.ServiceNotCompatibleWithVehicle(serviceDetail.getCatalogId(), vehicle.getVin());
                }
                bookingDetailService.addServiceToBooking(booking.getId(), serviceDetail);
            }
        }

        booking = bookingRepository.save(booking);

        // Tạo invoice tự động sau khi tạo booking
        invoiceService.create(booking.getId());

        // Trả về DTO có chi tiết dịch vụ và invoice
        return BookingResponseMapper.toDtoFull(booking, request.getScheduleDateTime());
    }

    private void checkBookingSingleDayLimit(Long id, LocalDate localDate) {
        List<Booking> bookingsOnDate = bookingRepository.findByCustomerIdAndDate(id, localDate)
                .stream()
                .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
                .collect(Collectors.toList());
        if(!bookingsOnDate.isEmpty()) {
            throw new CommonException.InvalidOperation("Khách hàng đã đạt giới hạn đặt lịch trong ngày: " + localDate);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));
        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "BOOKING", "read");

        // Trả về DTO đầy đủ (Booking + Details + Invoice + Lines)
        return BookingResponseMapper.toDtoFull(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomerId(Long customerId) {
        if (!userRepository.existsById(customerId))
            throw new CommonException.NotFound("User", customerId);
        accessControlService.verifyResourceAccess(customerId, "BOOKING", "read");

        // Trả về DTO tóm tắt (Summary)
        return bookingRepository.findByCustomerId(customerId)
                .stream().map(BookingResponseMapper::toDtoSummary).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByVehicleVin(String vin) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
                .orElseThrow(() -> new CommonException.NotFound("Vehicle", vin));
        accessControlService.verifyResourceAccess(vehicle.getCustomer().getId(), "BOOKING", "read");

        // Trả về DTO tóm tắt (Summary)
        return bookingRepository.findByVehicleVin(vin)
                .stream().map(BookingResponseMapper::toDtoSummary).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookingsFiltered(BookingStatus bookingStatus) {

        accessControlService.verifyCanAccessAllResources("BOOKING", "read");

        List<Booking> bookings = bookingRepository.findWithFilters(bookingStatus);

        // Trả về DTO tóm tắt (Summary)
        return bookings.stream()
                .map(BookingResponseMapper::toDtoSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse.CatalogDetail> getRecentlyUsedServicesByVehicle(String vin, int limit) {
        log.info("Fetching recently used services for vehicle: {}", vin);

        // Verify vehicle exists
        Vehicle vehicle = vehicleRepository.findByVin(vin)
                .orElseThrow(() -> new CommonException.NotFound("Vehicle", vin));

        // Access control
        accessControlService.verifyResourceAccess(vehicle.getCustomer().getId(), "BOOKING", "read");

        // Lấy các booking đã hoàn thành (DELIVERED hoặc MAINTENANCE_COMPLETE)
        List<Booking> completedBookings = bookingRepository.findByVehicleVin(vin).stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.MAINTENANCE_COMPLETE)
                .sorted((b1, b2) -> b2.getUpdatedAt().compareTo(b1.getUpdatedAt())) // Sort by newest first
                .collect(Collectors.toList());

        // Extract service details và loại bỏ trùng lặp theo serviceId
        List<BookingResponse.CatalogDetail> recentServices = completedBookings.stream()
                .flatMap(booking -> booking.getBookingDetails().stream())
                .map(detail -> BookingResponse.CatalogDetail.builder()
                        .id(detail.getId())
                        .catalogId(detail.getCatalogModel().getMaintenanceCatalog().getId())
                        .serviceName(detail.getCatalogModel().getMaintenanceCatalog().getName())
                        .description(detail.getDescription())
                        .build())
                .distinct() // Remove duplicates based on serviceId
                .limit(limit > 0 ? limit : 10) // Default limit 10
                .collect(Collectors.toList());

        log.info("Found {} recently used services for vehicle {}", recentServices.size(), vin);
        return recentServices;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse.CatalogDetail> getRecentlyUsedServicesByCustomer(Long customerId, int limit) {
        log.info("Fetching recently used services for customer: {}", customerId);

        // Verify customer exists
        if (!userRepository.existsById(customerId)) {
            throw new CommonException.NotFound("User", customerId);
        }

        // Access control
        accessControlService.verifyResourceAccess(customerId, "BOOKING", "read");

        // Lấy các booking đã hoàn thành
        List<Booking> completedBookings = bookingRepository.findByCustomerId(customerId).stream()
                .filter(b -> b.getBookingStatus() == BookingStatus.MAINTENANCE_COMPLETE)
                .sorted((b1, b2) -> b2.getUpdatedAt().compareTo(b1.getUpdatedAt()))
                .collect(Collectors.toList());

        // Extract service details
        List<BookingResponse.CatalogDetail> recentServices = completedBookings.stream()
                .flatMap(booking -> booking.getBookingDetails().stream())
                .map(detail -> BookingResponse.CatalogDetail.builder()
                        .id(detail.getId())
                        .catalogId(detail.getCatalogModel().getMaintenanceCatalog().getId())
                        .serviceName(detail.getCatalogModel().getMaintenanceCatalog().getName())
                        .description(detail.getDescription())
                        .build())
                .distinct()
                .limit(limit > 0 ? limit : 10)
                .collect(Collectors.toList());

        log.info("Found {} recently used services for customer {}", recentServices.size(), customerId);
        return recentServices;
    }

    @Override
    @Transactional
    public BookingResponse updateBooking(Long id, BookingRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Booking", id));

        accessControlService.verifyResourceAccess(booking.getCustomer().getId(), "BOOKING", "update");

        // Không cho update nếu đã được duyệt, đã hoàn thành hoặc đã hủy
        if (booking.getBookingStatus() == BookingStatus.MAINTENANCE_COMPLETE
                || booking.getBookingStatus() == BookingStatus.CANCELLED
                || booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            throw new CommonException.InvalidOperation("Không được cập nhật đơn với trạng thái này: " + booking.getBookingStatus());
        }

        if(request.getCustomerId() != null || !request.getCustomerId().equals(booking.getCustomer().getId())) {
            throw new CommonException.InvalidOperation("Không thể thay đổi khách hàng của đơn đặt lịch");
        }

        if(request.getVehicleVin() != null || !request.getVehicleVin().equals(booking.getVehicle().getVin())) {
            throw new CommonException.InvalidOperation("Không thể thay đổi xe của đơn đặt lịch");
        }

        boolean catalogDetailsUpdated = false;

        if (request.getScheduleDateTime() != null) {
            LocalDateTime scheduleDate = checkFutureScheduleDate(request.getScheduleDateTime());
            booking.setScheduleDate(scheduleDate);
        }

        if (request.getCatalogDetails() != null) {
            bookingDetailService.updateBookingServices(id, request.getCatalogDetails());
            catalogDetailsUpdated = true;
        }

        booking = bookingRepository.save(booking);

        // Cập nhật invoice nếu có thay đổi về dịch vụ
        if (catalogDetailsUpdated) {
            try {
                invoiceService.updateInvoiceFromBooking(booking.getId());
                log.info("Invoice updated successfully for booking {}", booking.getId());
            } catch (Exception e) {
                log.error("Failed to update invoice for booking {}: {}", booking.getId(), e.getMessage());
                // Không block việc update booking nếu invoice update thất bại
            }
        }

        ScheduleDateTime responseSchedule = request.getScheduleDateTime() != null
                ? request.getScheduleDateTime()
                : ScheduleDateTimeParser.format(booking.getScheduleDate(), "yyyy-MM-dd HH:mm:ss", "Asia/Ho_Chi_Minh");

        // Trả về DTO có chi tiết dịch vụ
        return BookingResponseMapper.toDtoWithDetails(booking, responseSchedule);
    }

    private LocalDateTime checkFutureScheduleDate(ScheduleDateTime scheduleDate) {
        LocalDateTime bookingDate = ScheduleDateTimeParser.parse(scheduleDate);
        if (bookingDate.isBefore(LocalDateTime.now())) {
            throw new CommonException.InvalidOperation("Thời gian đăt đơn phải ở tương lai");
        }

        if(bookingDate.isAfter(LocalDateTime.now().plusDays(7))) {
            throw new CommonException.InvalidOperation("Thời gian đăt đơn phải cách 1 tuần so với hiện tại");
        }

        return bookingDate;
    }

    private void checkAvailableSlot(LocalDateTime bookingDate) {
        if(bookingDate.getHour() < 7 ||  bookingDate.getHour() > 17) {
            throw new CommonException.InvalidOperation("Thời gian đặt lịch không hỗ trợ");
        }

        List<Booking> existingBookings = bookingRepository.findByScheduleDate(bookingDate)
                .stream()
                .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED && b.getBookingStatus() != BookingStatus.REJECTED)
                .collect(Collectors.toList());


        if(existingBookings.size() == 5) {
            throw new CommonException.InvalidOperation("Thời gian đặt đơn đã bị đầy");
        }
    }

}

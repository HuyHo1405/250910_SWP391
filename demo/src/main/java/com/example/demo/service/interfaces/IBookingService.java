package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.modelEnum.BookingLifecycle;
import com.example.demo.model.modelEnum.MaintenanceStatus;
import com.example.demo.model.modelEnum.PaymentStatus;
import com.example.demo.model.modelEnum.ScheduleStatus;

import java.util.List;

public interface IBookingService {
    BookingResponse createBooking(BookingRequest request);
    BookingResponse getBookingById(Long id);
    List<BookingResponse> getBookingsByCustomerId(Long customerId);

    List<BookingResponse> getBookingsByVehicleVin(String vin);

    List<BookingResponse> getAllBookingsFiltered(
            BookingLifecycle lifecycleStatus,
            ScheduleStatus scheduleStatus,
            MaintenanceStatus maintenanceStatus,
            PaymentStatus paymentStatus
    );
    BookingResponse updateBooking(Long id, BookingRequest request);
    void deleteBooking(Long id);
    BookingResponse cancelBooking(Long id, String reason);
    BookingResponse completeBooking(Long id);
}
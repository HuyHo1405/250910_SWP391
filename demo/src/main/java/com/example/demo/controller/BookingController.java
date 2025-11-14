package com.example.demo.controller;

import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.dto.DailyBookedSlot;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.service.interfaces.IBookingService;
import com.example.demo.service.interfaces.IBookingSlotService;
import com.example.demo.service.interfaces.IBookingStatusService;
import com.example.demo.model.dto.EnumSchemaResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking")
public class BookingController {
    private final IBookingService bookingService;
    private final IBookingStatusService bookingStatusService;
    private final IBookingSlotService bookingSlotService;

    private static final String VIN_REGEX = "^[A-HJ-NPR-Z0-9]{17}$";

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse resp = bookingService.createBooking(request);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id) {
        BookingResponse resp = bookingService.getBookingById(id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByCustomer(@PathVariable Long customerId) {
        List<BookingResponse> responses = bookingService.getBookingsByCustomerId(customerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/vehicle/{vin}")
    public ResponseEntity<List<BookingResponse>> getBookingsByVehicle(
            @PathVariable
            @Pattern(regexp = VIN_REGEX, message = "Mã vin không đúng định dạng")
            String vin
    ) {
        List<BookingResponse> responses = bookingService.getBookingsByVehicleVin(vin);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings(
            @RequestParam(required = false) BookingStatus bookingStatus
    ) {
        List<BookingResponse> responses = bookingService.getAllBookingsFiltered(
                bookingStatus
        );
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @RequestBody @Valid BookingRequest request) {
        BookingResponse resp = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id) {
        BookingResponse resp = bookingStatusService.cancelBooking(id);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/rejected")
    public ResponseEntity<BookingResponse> rejectBooking(
            @PathVariable Long id) {
        BookingResponse resp = bookingStatusService.rejectBooking(id);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long id) {
        BookingResponse resp = bookingStatusService.confirmBooking(id);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/start-maintenance")
    public ResponseEntity<BookingResponse> startMaintenance(@PathVariable Long id) {
        BookingResponse resp = bookingStatusService.startMaintenance(id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/slots")
    public ResponseEntity<List<DailyBookedSlot>> getBookedSlots() {
        List<DailyBookedSlot> slots = bookingSlotService.getBookedSlot();
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/{id}/check-parts")
    public ResponseEntity<Boolean> checkAvailablePart(
            @PathVariable("id") Long bookingId
    ) {
        boolean isSufficient = bookingStatusService.checkEnoughPartForBooking(bookingId);
        return ResponseEntity.ok(isSufficient);
    }

    @GetMapping("/working-hours")
    public ResponseEntity<EnumSchemaResponse> getWorkingHours() {
        List<String> hours = new ArrayList<>();
        for (int i = 7; i <= 17; i++) {
            hours.add(String.format("%02d:00", i));
        }

        EnumSchemaResponse response = new EnumSchemaResponse(
                "workingHours",
                hours,
                "Danh sách giờ làm việc có thể đặt lịch (từ 07:00 đến 17:00)"
        );

        return ResponseEntity.ok(response);
    }
}

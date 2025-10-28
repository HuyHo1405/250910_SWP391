package com.example.demo.controller;

import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.service.interfaces.IBookingService;
import com.example.demo.service.interfaces.IBookingStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking")
public class BookingController {
    private final IBookingService bookingService;
    private final IBookingStatusService bookingStatusService;

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
    public ResponseEntity<List<BookingResponse>> getBookingsByVehicle(@PathVariable String vin) {
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


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        BookingResponse resp = bookingStatusService.cancelBooking(id, reason);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long id) {
        BookingResponse resp = bookingStatusService.confirmBooking(id);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/start-maintenance")
    public ResponseEntity<BookingResponse> startMaintenance(@PathVariable Long id) {
        BookingResponse resp = bookingStatusService.startMaintenance(id);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<BookingResponse> completeBooking(@PathVariable Long id) {
        BookingResponse resp = bookingStatusService.completeMaintenance(id);
        return ResponseEntity.ok(resp);
    }
}

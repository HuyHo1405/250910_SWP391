package com.example.demo.controller;

import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.entity.BookingStatus;
import com.example.demo.service.interfaces.IBookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking")
public class BookingController {

    private final IBookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(response);
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

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingRequest request) {
        BookingResponse response = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status/{bookingStatus}")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @PathVariable BookingStatus bookingStatus) {
        BookingResponse response = bookingService.updateBookingStatus(id, bookingStatus);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/total-price")
    public ResponseEntity<Double> calculateTotalPrice(@PathVariable Long id) {
        Double totalPrice = bookingService.calculateTotalPrice(id);
        return ResponseEntity.ok(totalPrice);
    }
}

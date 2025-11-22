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
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Booking", description = "Endpoints for managing bookings - Staff, Customer")
public class BookingController {
    private final IBookingService bookingService;
    private final IBookingStatusService bookingStatusService;
    private final IBookingSlotService bookingSlotService;

    private static final String VIN_REGEX = "^[A-HJ-NPR-Z0-9]{17}$";

    @PostMapping
    @Operation(summary = "Create a new booking", description = "Allows a logged-in customer to create a new booking.")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse resp = bookingService.createBooking(request);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by id", description = "Returns booking details by booking id.")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable Long id) {
        BookingResponse resp = bookingService.getBookingById(id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get bookings by customer", description = "Returns all bookings for a specific customer.")
    public ResponseEntity<List<BookingResponse>> getBookingsByCustomer(@PathVariable Long customerId) {
        List<BookingResponse> responses = bookingService.getBookingsByCustomerId(customerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/vehicle/{vin}")
    @Operation(summary = "Get bookings by vehicle VIN", description = "Returns all bookings for a specific vehicle by VIN.")
    public ResponseEntity<List<BookingResponse>> getBookingsByVehicle(
            @PathVariable
            @Pattern(regexp = VIN_REGEX, message = "Mã vin không đúng định dạng")
            String vin
    ) {
        List<BookingResponse> responses = bookingService.getBookingsByVehicleVin(vin);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "Get all bookings (filtered)", description = "Returns all bookings, optionally filtered by status.")
    public ResponseEntity<List<BookingResponse>> getAllBookings(
            @RequestParam(required = false) BookingStatus bookingStatus
    ) {
        List<BookingResponse> responses = bookingService.getAllBookingsFiltered(
                bookingStatus
        );
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a booking", description = "Updates an existing booking by id.")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @RequestBody @Valid BookingRequest request) {
        BookingResponse resp = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking", description = "Cancels a booking by id.")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id) {
        BookingResponse resp = bookingStatusService.cancelBooking(id);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/rejected")
    @Operation(summary = "Reject a booking", description = "Rejects a booking by id.")
    public ResponseEntity<BookingResponse> rejectBooking(
            @PathVariable Long id) {
        BookingResponse resp = bookingStatusService.rejectBooking(id);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirm a booking", description = "Confirms a booking by id.")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long id) {
        BookingResponse resp = bookingStatusService.confirmBooking(id);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/assign-technician")
    @Operation(summary = "Assign technician to booking", description = "Assigns a technician to a booking.")
    public ResponseEntity<BookingResponse> assignTechnician(@PathVariable Long id, @RequestParam Long technicianId) {
        BookingResponse resp = bookingStatusService.assignTechnician(id, technicianId);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}/reassign-technician")
    @Operation(summary = "Reassign technician to booking", description = "Reassigns a technician to a booking with a reason.")
    public ResponseEntity<BookingResponse> reassignTechnician(@PathVariable Long id, @RequestParam Long technicianId, @RequestParam String reason) {
        BookingResponse resp = bookingStatusService.reassignTechnician(id, technicianId, reason);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/slots")
    @Operation(summary = "Get booked slots", description = "Returns all booked slots for all days.")
    public ResponseEntity<List<DailyBookedSlot>> getBookedSlots() {
        List<DailyBookedSlot> slots = bookingSlotService.getBookedSlot();
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/{id}/check-parts")
    @Operation(summary = "Check available parts for booking", description = "Checks if there are enough parts for a booking.")
    public ResponseEntity<Boolean> checkAvailablePart(
            @PathVariable("id") Long bookingId
    ) {
        boolean isSufficient = bookingStatusService.checkEnoughPartForBooking(bookingId);
        return ResponseEntity.ok(isSufficient);
    }

    @GetMapping("/working-hours")
    @Operation(summary = "Get working hours", description = "Returns the list of available working hours for booking.")
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

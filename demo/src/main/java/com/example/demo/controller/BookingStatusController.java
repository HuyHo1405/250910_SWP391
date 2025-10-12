package com.example.demo.controller;

import com.example.demo.model.dto.BookingResponse;
import com.example.demo.service.interfaces.IBookingStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings-status")
@RequiredArgsConstructor
@Tag(name = "Booking Status")
public class BookingStatusController {
    private final IBookingStatusService bookingStatusService;

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirm(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingStatusService.confirm(id, reason));
    }

    @PatchMapping("/{id}/checkin")
    public ResponseEntity<BookingResponse> checkin(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingStatusService.checkin(id, reason));
    }

    @PatchMapping("/{id}/inspect")
    public ResponseEntity<BookingResponse> inspect(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingStatusService.inspect(id, reason));
    }

    @PatchMapping("/{id}/wait-for-approval")
    public ResponseEntity<BookingResponse> waitForApproval(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingStatusService.waitForApproval(id, reason));
    }

    @PatchMapping("/{id}/start-service")
    public ResponseEntity<BookingResponse> startService(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingStatusService.startService(id, reason));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<BookingResponse> complete(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingStatusService.complete(id, reason));
    }

    @PatchMapping("/{id}/delivered")
    public ResponseEntity<BookingResponse> delivered(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingStatusService.delivered(id, reason));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingStatusService.cancel(id, reason));
    }
}

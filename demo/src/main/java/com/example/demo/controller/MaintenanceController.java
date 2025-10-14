package com.example.demo.controller;

import com.example.demo.model.dto.BookingResponse;
import com.example.demo.service.interfaces.IMaintenanceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings/{bookingId}/maintenance")
@RequiredArgsConstructor
@Tag(name = "Maintenance")
public class MaintenanceController {
    private final IMaintenanceService maintenanceService;

    @PostMapping("/start-inspection")
    public ResponseEntity<BookingResponse> startInspection(@PathVariable Long bookingId) {
        return ResponseEntity.ok(maintenanceService.startInspection(bookingId));
    }

    @PostMapping("/request-approval")
    public ResponseEntity<BookingResponse> requestApproval(@PathVariable Long bookingId) {
        return ResponseEntity.ok(maintenanceService.requestApproval(bookingId));
    }

    @PostMapping("/approve")
    public ResponseEntity<BookingResponse> approve(@PathVariable Long bookingId) {
        return ResponseEntity.ok(maintenanceService.approve(bookingId));
    }

    @PostMapping("/reject")
    public ResponseEntity<BookingResponse> reject(@PathVariable Long bookingId, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(maintenanceService.reject(bookingId, reason));
    }

    @PostMapping("/complete")
    public ResponseEntity<BookingResponse> complete(@PathVariable Long bookingId) {
        return ResponseEntity.ok(maintenanceService.complete(bookingId));
    }

    @PostMapping("/abort")
    public ResponseEntity<BookingResponse> abort(@PathVariable Long bookingId, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(maintenanceService.abort(bookingId, reason));
    }

    @PostMapping("/cancel")
    public ResponseEntity<BookingResponse> cancelMaintenance(@PathVariable Long bookingId, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(maintenanceService.cancelMaintenance(bookingId, reason));
    }
}

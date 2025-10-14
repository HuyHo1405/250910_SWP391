package com.example.demo.controller;

import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.dto.ScheduleDateTime;
import com.example.demo.service.interfaces.IScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings/{bookingId}/schedule")
@RequiredArgsConstructor
@Tag(name = "Schedule")
public class ScheduleController {
    private final IScheduleService scheduleService;

    @PostMapping("/confirm")
    public ResponseEntity<BookingResponse> confirm(@PathVariable Long bookingId) {
        return ResponseEntity.ok(scheduleService.confirm(bookingId));
    }

    @PostMapping("/check-in")
    public ResponseEntity<BookingResponse> checkIn(@PathVariable Long bookingId) {
        return ResponseEntity.ok(scheduleService.checkIn(bookingId));
    }

    @PostMapping("/reschedule")
    public ResponseEntity<BookingResponse> reschedule(@PathVariable Long bookingId, @Valid @RequestBody ScheduleDateTime newTime) {
        return ResponseEntity.ok(scheduleService.reschedule(bookingId, newTime));
    }

    @PostMapping("/no-show")
    public ResponseEntity<BookingResponse> noShow(@PathVariable Long bookingId) {
        return ResponseEntity.ok(scheduleService.noShow(bookingId));
    }

    @PostMapping("/cancel")
    public ResponseEntity<BookingResponse> cancelSchedule(@PathVariable Long bookingId, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(scheduleService.cancelSchedule(bookingId, reason));
    }

    @PostMapping("/reject")
    public ResponseEntity<BookingResponse> rejectSchedule(@PathVariable Long bookingId, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(scheduleService.cancelSchedule(bookingId, reason));
    }
}


package com.example.demo.controller;

import com.example.demo.model.dto.BookingResponse;
import com.example.demo.service.interfaces.IPaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings/{bookingId}/payment")
@RequiredArgsConstructor
@Tag(name = "Payment")
public class PaymentController {
    private final IPaymentService paymentService;

    @PostMapping("/authorize")
    public ResponseEntity<BookingResponse> authorize(@PathVariable Long bookingId, @RequestParam Double amount) {
        return ResponseEntity.ok(paymentService.authorize(bookingId, amount));
    }

    @PostMapping("/pay")
    public ResponseEntity<BookingResponse> pay(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.pay(bookingId));
    }

    @PostMapping("/refund")
    public ResponseEntity<BookingResponse> refund(@PathVariable Long bookingId, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(paymentService.refund(bookingId, reason));
    }

    @PostMapping("/void")
    public ResponseEntity<BookingResponse> voidPayment(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.voidPayment(bookingId));
    }

    @PostMapping("/cancel")
    public ResponseEntity<BookingResponse> cancelPayment(@PathVariable Long bookingId, @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(paymentService.cancelPayment(bookingId, reason));
    }
}

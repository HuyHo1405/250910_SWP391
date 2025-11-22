package com.example.demo.controller;

import com.example.demo.model.dto.PaymentRequest;
import com.example.demo.model.dto.PaymentResponse;
import com.example.demo.service.interfaces.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Endpoints for managing payments - Customer, Staff")
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/create-payment")
    @Operation(summary = "Create payment URL", description = "Allows a logged-in customer to create a payment URL for an order.")
    public ResponseEntity<PaymentResponse.PaymentURL> createPayment(
            @Valid @RequestBody PaymentRequest.CreatePayment request
    ) {
        // Gọi service để tạo link
        PaymentResponse.PaymentURL response = paymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }

//    /**
//     * API để Server VNPAY gọi "ngầm" (Instant Payment Notification - IPN).
//     * Đây là nơi quan trọng nhất để cập nhật trạng thái đơn hàng (SUCCESSFUL/FAILED).
//     *
//     * @param vnpayParams Map chứa tất cả các tham số VNPAY gửi sang.
//     * @return DTO (VnpayIpn) chứa RspCode và Message để VNPAY biết kết quả.
//     */
//    @PostMapping("/vnpay-ipn")
//    @Operation(summary = "Handle VNPAY IPN", description = "Handles VNPAY Instant Payment Notification (IPN) callback to update order status.")
//    public ResponseEntity<PaymentResponse.VnpayIpn> handleVnpayIpn(
//            @RequestParam Map<String, String> vnpayParams
//    ) {
//        // Gọi service để xử lý IPN
//        PaymentResponse.VnpayIpn response = paymentService.handleVnpayIpn(vnpayParams);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/history/{bookingId}")
    @Operation(summary = "Get payment history for booking", description = "Returns the payment transaction history for a specific booking. Requires customer authentication.")
    public ResponseEntity<List<PaymentResponse.Transaction>> getPaymentHistoryForBooking(
            @PathVariable("bookingId") Long bookingId
    ) {
        // 1. Gọi service method mà chúng ta đã định nghĩa
        List<PaymentResponse.Transaction> historyList = paymentService.getPaymentHistory(bookingId);

        // 2. Trả về HTTP 200 OK cùng với danh sách lịch sử
        return ResponseEntity.ok(historyList);
    }

    @GetMapping("/status")
    @Operation(summary = "Check payment status", description = "Checks the payment status for a given order code. Requires customer authentication.")
    public ResponseEntity<PaymentResponse.PaymentStatusDetail> checkPaymentStatus(
            @RequestParam("orderCode") String orderCode
    ) {
        // Gọi service để kiểm tra trạng thái
        PaymentResponse.PaymentStatusDetail response = paymentService.checkPaymentStatus(orderCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/simulate-ipn-success")
    @Operation(summary = "Simulate IPN success", description = "Simulates a successful IPN callback for payment. This endpoint is called by the system after customer payment to update order status.")
    public ResponseEntity<?> testSuccessIpn(@RequestParam String orderCode) {
        PaymentResponse.VnpayIpn response = paymentService.simulateIpnSuccess(orderCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/simulate-ipn-fail")
    @Operation(summary = "Simulate IPN fail", description = "Simulates a failed IPN callback for payment. This endpoint is called by the system after customer payment to update order status.")
    public ResponseEntity<?> testFailIpn(@RequestParam String orderCode) {
        PaymentResponse.VnpayIpn response = paymentService.simulateIpnFail(orderCode);
        return ResponseEntity.ok(response);
    }

}

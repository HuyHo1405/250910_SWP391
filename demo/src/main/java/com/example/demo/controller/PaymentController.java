package com.example.demo.controller;

import com.example.demo.model.dto.PaymentRequest;
import com.example.demo.model.dto.PaymentResponse;
import com.example.demo.service.interfaces.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor // Tự động inject IPaymentService qua constructor
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/create-payment")
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
//    public ResponseEntity<PaymentResponse.VnpayIpn> handleVnpayIpn(
//            @RequestParam Map<String, String> vnpayParams
//    ) {
//        // Gọi service để xử lý IPN
//        PaymentResponse.VnpayIpn response = paymentService.handleVnpayIpn(vnpayParams);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/history/{bookingId}")
    public ResponseEntity<List<PaymentResponse.Transaction>> getPaymentHistoryForBooking(
            @PathVariable("bookingId") Long bookingId
    ) {
        // 1. Gọi service method mà chúng ta đã định nghĩa
        List<PaymentResponse.Transaction> historyList = paymentService.getPaymentHistory(bookingId);

        // 2. Trả về HTTP 200 OK cùng với danh sách lịch sử
        return ResponseEntity.ok(historyList);
    }

    /**
     * API để Frontend gọi (ở trang "Kết quả thanh toán") để kiểm tra trạng thái.
     * Frontend sẽ lấy orderCode (vnp_TxnRef) từ URL và gọi API này.
     *
     * @param orderCode Mã đơn hàng (vnp_TxnRef)
     * @return DTO chứa trạng thái chi tiết (PENDING, SUCCESSFUL, FAILED).
     */
    @GetMapping("/status")
    public ResponseEntity<PaymentResponse.PaymentStatusDetail> checkPaymentStatus(
            @RequestParam("orderCode") String orderCode
    ) {
        // Gọi service để kiểm tra trạng thái
        PaymentResponse.PaymentStatusDetail response = paymentService.checkPaymentStatus(orderCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/simulate-ipn-success")
    public ResponseEntity<?> testIpn(@RequestParam String orderCode) {
        PaymentResponse.VnpayIpn response = paymentService.simulateIpnSuccess(orderCode);
        return ResponseEntity.ok(response);
    }
}

package com.example.demo.service.interfaces;

import com.example.demo.model.dto.PaymentRequest;
import com.example.demo.model.dto.PaymentResponse;

import java.util.List;
import java.util.Map;

public interface IPaymentService {
    PaymentResponse.PaymentURL createPayment(PaymentRequest.CreatePayment request);

    PaymentResponse.VnpayIpn handleVnpayIpn(Map<String, String> vnpayParams);

    PaymentResponse.PaymentStatusDetail checkPaymentStatus(String orderCode);

    PaymentResponse.VnpayIpn simulateIpnSuccess(String orderCode);

    PaymentResponse.VnpayIpn simulateIpnFail(String orderCode);

    List<PaymentResponse.Transaction> getPaymentHistory(Long bookingId);
    List<PaymentResponse.Transaction> getCustomerPaymentHistory(Long customerId);
}
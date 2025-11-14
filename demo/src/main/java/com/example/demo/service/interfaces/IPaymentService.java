package com.example.demo.service.interfaces;

import com.example.demo.model.dto.PaymentRequest;
import com.example.demo.model.dto.PaymentResponse;

import java.util.Map;

public interface IPaymentService {
    PaymentResponse.PaymentURL createPayment(PaymentRequest.CreatePayment request);

    PaymentResponse.VnpayIpn handleVnpayIpn(Map<String, String> vnpayParams);

    PaymentResponse.PaymentStatusDetail checkPaymentStatus(String orderCode);

    PaymentResponse.VnpayIpn simulateIpnSuccess(String orderCode);

}

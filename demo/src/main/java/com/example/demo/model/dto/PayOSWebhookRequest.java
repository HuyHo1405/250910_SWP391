package com.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayOSWebhookRequest {
    private String code;
    private String desc;
    private String signature;
    private PayOSWebhookPayload data;
}

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
class PayOSWebhookPayload {
    private String orderCode;
    private String paymentLinkId;
    private long amount;
    private String description;
    private String method;
    private String status;
}


package com.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String vehicleVin;
    private String vehicleModel;
    private ScheduleDateTime scheduleDateTime;
    private String bookingStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ServiceDetail> serviceDetails;

    private InvoiceResponse invoice;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceDetail {
        private Long id;
        private Long serviceId;
        private String serviceName;
        private String description;
    }
}

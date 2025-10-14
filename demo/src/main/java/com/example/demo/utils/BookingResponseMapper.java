package com.example.demo.utils;

import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.dto.ScheduleDateTime;
import com.example.demo.model.entity.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class BookingResponseMapper {
    public static BookingResponse toDto(Booking booking, ScheduleDateTime scheduleDateTime) {
        List<BookingResponse.ServiceDetail> serviceDetails = booking.getBookingDetails().stream()
                .map(detail -> BookingResponse.ServiceDetail.builder()
                        .id(detail.getId())
                        .serviceId(detail.getService().getId())
                        .serviceName(detail.getService().getName())
                        .description(detail.getDescription())
                        .servicePrice(detail.getServicePrice())
                        .build())
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer().getId())
                .customerName(booking.getCustomer().getFullName())
                .vehicleVin(booking.getVehicle().getVin())
                .vehicleModel(booking.getVehicle().getModel().getModelName())
                .scheduleDateTime(scheduleDateTime)
                .scheduleStatus(booking.getScheduleStatus() != null ? booking.getScheduleStatus().name() : null)
                .maintenanceStatus(booking.getMaintenanceStatus() != null ? booking.getMaintenanceStatus().name() : null)
                .paymentStatus(booking.getPaymentStatus() != null ? booking.getPaymentStatus().name() : null)
                .lifecycleStatus(booking.getLifecycleStatus() != null ? booking.getLifecycleStatus().name() : null)
                .totalPrice(booking.getTotalPrice())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .serviceDetails(serviceDetails)
                .build();
    }

    // Overload dùng khi muốn tự format scheduleDateTime theo timezone default
    public static BookingResponse toDto(Booking booking) {
        return toDto(
                booking,
                ScheduleDateTimeParser.format(booking.getScheduleDate(), "yyyy-MM-dd HH:mm:ss", "Asia/Ho_Chi_Minh")
        );
    }
}

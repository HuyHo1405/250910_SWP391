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
                .status(booking.getStatus().name())
                .paymentStatus(booking.getPaymentStatus().name()) // nhớ bổ sung!
                .totalPrice(booking.getTotalPrice())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .serviceDetails(serviceDetails)
                .build();
    }
    // overload nếu muốn truyền booking → dto luôn không cần scheduleDateTime
    public static BookingResponse toDto(Booking booking) {
        return toDto(booking,
                ScheduleDateTimeParser.format(booking.getScheduleDate(), "yyyy-MM-dd HH:mm:ss", "Asia/Ho_Chi_Minh"));
    }
}

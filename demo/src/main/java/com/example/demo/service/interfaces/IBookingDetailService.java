package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.entity.BookingDetail;

import java.util.List;

public interface IBookingDetailService {

    BookingDetail addServiceToBooking(Long bookingId, BookingRequest.CatalogDetail serviceDetail);

    void removeServiceFromBooking(Long bookingId, Long serviceId);

    void updateBookingServices(Long bookingId, List<BookingRequest.CatalogDetail> serviceDetails);

    List<BookingDetail> getBookingDetailsByBookingId(Long bookingId);

    BookingDetail updateServiceDescription(Long detailId, String description);
}

package com.example.demo.service.interfaces;

import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.dto.BookingResponse;
import com.example.demo.model.entity.BookingStatus;

import java.util.List;

public interface IBookingService {

    BookingResponse createBooking(BookingRequest request);

    BookingResponse getBookingById(Long id);

    List<BookingResponse> getBookingsByCustomerId(Long customerId);

    List<BookingResponse> getBookingsByVehicleVin(String vin);

    BookingResponse updateBooking(Long id, BookingRequest request);

    BookingResponse updateBookingStatus(Long id, BookingStatus bookingStatus);

    void cancelBooking(Long id);

    Double calculateTotalPrice(Long id);
}
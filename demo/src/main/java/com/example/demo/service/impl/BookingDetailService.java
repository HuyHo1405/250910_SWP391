package com.example.demo.service.impl;

import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.BookingDetail;
import com.example.demo.model.entity.MaintenanceCatalog;
import com.example.demo.repo.BookingDetailRepo;
import com.example.demo.repo.BookingRepo;
import com.example.demo.repo.MaintenanceCatalogRepo;
import com.example.demo.service.interfaces.IBookingDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingDetailService implements IBookingDetailService {

    private final BookingDetailRepo bookingDetailRepository;
    private final BookingRepo bookingRepository;
    private final MaintenanceCatalogRepo serviceRepository;

    @Override
    public BookingDetail addServiceToBooking(Long bookingId, BookingRequest.ServiceDetail serviceDetail) {
        log.info("Adding service ID: {} to booking ID: {}", serviceDetail.getServiceId(), bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        MaintenanceCatalog service = serviceRepository.findById(serviceDetail.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + serviceDetail.getServiceId()));

        BookingDetail bookingDetail = BookingDetail.builder()
                .booking(booking)
                .service(service)
                .description(serviceDetail.getDescription())
                .servicePrice(0.1)//TODO fix this
                .build();

        booking.addBookingDetail(bookingDetail);
        bookingDetail = bookingDetailRepository.save(bookingDetail);

        log.info("Service added successfully to booking");
        return bookingDetail;
    }

    @Override
    public void removeServiceFromBooking(Long bookingId, Long serviceId) {
        log.info("Removing service ID: {} from booking ID: {}", serviceId, bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        BookingDetail bookingDetail = bookingDetailRepository
                .findByBookingIdAndServiceId(bookingId, serviceId)
                .orElseThrow(() -> new RuntimeException("Booking detail not found"));

        booking.removeBookingDetail(bookingDetail);
        bookingDetailRepository.delete(bookingDetail);

        log.info("Service removed successfully from booking");
    }

    @Override
    public void updateBookingServices(Long bookingId, List<BookingRequest.ServiceDetail> serviceDetails) {
        log.info("Updating services for booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        // Clear existing booking details
        List<BookingDetail> existingDetails = booking.getBookingDetails();
        existingDetails.clear();
        bookingDetailRepository.deleteByBookingId(bookingId);

        // Add new booking details
        for (BookingRequest.ServiceDetail serviceDetail : serviceDetails) {
            addServiceToBooking(bookingId, serviceDetail);
        }

        log.info("Services updated successfully for booking");
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateBookingTotal(Long bookingId) {
        log.info("Calculating total for booking ID: {}", bookingId);

        List<BookingDetail> bookingDetails = bookingDetailRepository.findByBookingId(bookingId);

        Double total = bookingDetails.stream()
                .mapToDouble(BookingDetail::getServicePrice)
                .sum();

        log.info("Total calculated: {}", total);
        return total;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDetail> getBookingDetailsByBookingId(Long bookingId) {
        log.info("Fetching booking details for booking ID: {}", bookingId);
        return bookingDetailRepository.findByBookingId(bookingId);
    }

    @Override
    public BookingDetail updateServiceDescription(Long detailId, String description) {
        log.info("Updating description for booking detail ID: {}", detailId);

        BookingDetail bookingDetail = bookingDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Booking detail not found with ID: " + detailId));

        bookingDetail.setDescription(description);
        bookingDetail = bookingDetailRepository.save(bookingDetail);

        log.info("Description updated successfully");
        return bookingDetail;
    }
}

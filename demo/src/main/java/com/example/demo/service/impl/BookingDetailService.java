package com.example.demo.service.impl;

import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.entity.*;
import com.example.demo.repo.*;
import com.example.demo.service.interfaces.IBookingDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingDetailService implements IBookingDetailService {

    private final BookingDetailRepo bookingDetailRepository;
    private final BookingRepo bookingRepository;
    private final MaintenanceCatalogRepo catalogRepository;
    private final VehicleModelRepo vehicleModelRepository;
    private final MaintenanceCatalogModelRepo  maintenanceCatalogModelRepository;

    @Override
    public BookingDetail addServiceToBooking(Long bookingId, BookingRequest.ServiceDetail serviceDetail) {
        log.info("Adding service ID: {} to booking ID: {}", serviceDetail.getServiceId(), bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt lịch với ID: " + bookingId));

        catalogRepository.findById(serviceDetail.getServiceId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với ID: " + serviceDetail.getServiceId()));

        vehicleModelRepository.findById(serviceDetail.getModelId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mẫu xe với ID: " + serviceDetail.getModelId()));

        MaintenanceCatalogModel catalogModel = maintenanceCatalogModelRepository
                .findByMaintenanceCatalogIdAndVehicleModelId(serviceDetail.getServiceId(), serviceDetail.getModelId())
                .orElseThrow(() -> new RuntimeException("Dịch vụ dành cho xe không tồn tại"));

        BookingDetail bookingDetail = BookingDetail.builder()
                .booking(booking)
                .catalogModel(catalogModel)
                .description(serviceDetail.getDescription())
                .build();

        booking.addBookingDetail(bookingDetail);
        bookingDetail = bookingDetailRepository.save(bookingDetail);

        log.info("Service added successfully to booking");
        return bookingDetail;
    }

    @Override
    public void removeServiceFromBooking(Long bookingId, Long bookingDetailId) {
        log.info("Removing detail ID: {} from booking ID: {}", bookingDetailId, bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt lịch với ID: " + bookingId));

        BookingDetail bookingDetail = bookingDetailRepository.findById(bookingDetailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đặt lịch"));

        booking.removeBookingDetail(bookingDetail);
        bookingDetailRepository.delete(bookingDetail);

        log.info("Service removed successfully from booking");
    }

    @Override
    public void updateBookingServices(Long bookingId, List<BookingRequest.ServiceDetail> serviceDetails) {
        log.info("Updating services for booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt lịch với ID: " + bookingId));

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
    public List<BookingDetail> getBookingDetailsByBookingId(Long bookingId) {
        log.info("Fetching booking details for booking ID: {}", bookingId);
        return bookingDetailRepository.findByBookingId(bookingId);
    }

    @Override
    public BookingDetail updateServiceDescription(Long detailId, String description) {
        log.info("Updating description for booking detail ID: {}", detailId);

        BookingDetail bookingDetail = bookingDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đặt lịch với ID: " + detailId));

        bookingDetail.setDescription(description);
        bookingDetail = bookingDetailRepository.save(bookingDetail);

        log.info("Description updated successfully");
        return bookingDetail;
    }
}

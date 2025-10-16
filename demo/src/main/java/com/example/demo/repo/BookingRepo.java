package com.example.demo.repo;

import com.example.demo.model.entity.Booking;
import com.example.demo.model.modelEnum.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    // Basic finders
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByVehicleVin(String vin);

    // Find by từng status chính
    List<Booking> findByBookingStatus(BookingStatus status);
    List<Booking> findByPaymentStatus(PaymentStatus status);

    // API lấy bookings theo filter chính (core)
    @Query("SELECT b FROM Booking b WHERE " +
            "(:bookingStatus IS NULL OR b.bookingStatus = :bookingStatus) AND " +
            "(:paymentStatus IS NULL OR b.paymentStatus = :paymentStatus)")
    List<Booking> findWithFilters(
            @Param("bookingStatus") BookingStatus bookingStatus,
            @Param("paymentStatus") PaymentStatus paymentStatus
    );

    // Find by date range (nếu dùng)
    List<Booking> findByScheduleDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find upcoming bookings (nếu cần load lịch), past bookings, today bookings
    List<Booking> findByScheduleDateAfter(LocalDateTime date);
    List<Booking> findByScheduleDateBefore(LocalDateTime date);

    // Count by main status
    Long countByBookingStatus(BookingStatus status);
    Long countByPaymentStatus(PaymentStatus status);

    // Revenue queries (nếu cần tổng doanh thu cho analytics core)
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.paymentStatus = :status")
    Double calculateTotalRevenueByPaymentStatus(@Param("status") PaymentStatus status);

}

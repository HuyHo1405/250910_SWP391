package com.example.demo.repo;

import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.BookingStatus;
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

    // Find by status
    List<Booking> findByStatus(BookingStatus status);

    // Find by customer and status
    List<Booking> findByCustomerIdAndStatus(Long customerId, BookingStatus status);

    // Find by vehicle and status
    List<Booking> findByVehicleVinAndStatus(String vin, BookingStatus status);

    // Find by date range
    List<Booking> findByScheduleDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find upcoming bookings
    List<Booking> findByScheduleDateAfter(LocalDateTime date);

    // Find past bookings
    List<Booking> findByScheduleDateBefore(LocalDateTime date);

    // Find today's bookings
    @Query("SELECT b FROM Booking b WHERE DATE(b.scheduleDate) = DATE(:date)")
    List<Booking> findBookingsByDate(@Param("date") LocalDateTime date);

    // Find bookings with pagination
    Page<Booking> findByCustomerId(Long customerId, Pageable pageable);
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);

    // Custom queries
    @Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId " +
            "AND b.scheduleDate BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsByCustomerAndDateRange(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT b FROM Booking b WHERE b.vehicle.vin = :vin " +
            "AND b.scheduleDate BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsByVehicleAndDateRange(
            @Param("vin") String vin,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Count queries
    Long countByStatus(BookingStatus status);
    Long countByCustomerId(Long customerId);
    Long countByVehicleVin(String vin);

    // Revenue queries
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status = :status")
    Double calculateTotalRevenueByStatus(@Param("status") BookingStatus status);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE " +
            "b.scheduleDate BETWEEN :startDate AND :endDate")
    Double calculateRevenueByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Statistics queries
    @Query("SELECT b.status, COUNT(b) FROM Booking b GROUP BY b.status")
    List<Object[]> getBookingCountByStatus();

    @Query("SELECT DATE(b.scheduleDate), COUNT(b) FROM Booking b " +
            "WHERE b.scheduleDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(b.scheduleDate) ORDER BY DATE(b.scheduleDate)")
    List<Object[]> getBookingCountByDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}

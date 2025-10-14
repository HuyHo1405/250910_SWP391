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

    // Find by status từng miền
    List<Booking> findByScheduleStatus(ScheduleStatus status);
    List<Booking> findByMaintenanceStatus(MaintenanceStatus status);
    List<Booking> findByPaymentStatus(PaymentStatus status);
    List<Booking> findByLifecycleStatus(BookingLifecycle status);

    // Find by customer and từng miền status
    List<Booking> findByCustomerIdAndScheduleStatus(Long customerId, ScheduleStatus status);
    List<Booking> findByCustomerIdAndMaintenanceStatus(Long customerId, MaintenanceStatus status);
    List<Booking> findByCustomerIdAndPaymentStatus(Long customerId, PaymentStatus status);
    List<Booking> findByCustomerIdAndLifecycleStatus(Long customerId, BookingLifecycle status);

    // Find by vehicle and từng miền status
    List<Booking> findByVehicleVinAndScheduleStatus(String vin, ScheduleStatus status);
    List<Booking> findByVehicleVinAndMaintenanceStatus(String vin, MaintenanceStatus status);
    List<Booking> findByVehicleVinAndPaymentStatus(String vin, PaymentStatus status);
    List<Booking> findByVehicleVinAndLifecycleStatus(String vin, BookingLifecycle status);
    @Query("SELECT b FROM Booking b WHERE " +
            "(:lifecycleStatus IS NULL OR b.lifecycleStatus = :lifecycleStatus) AND " +
            "(:scheduleStatus IS NULL OR b.scheduleStatus = :scheduleStatus) AND " +
            "(:maintenanceStatus IS NULL OR b.maintenanceStatus = :maintenanceStatus) AND " +
            "(:paymentStatus IS NULL OR b.paymentStatus = :paymentStatus)")
    List<Booking> findWithFilters(
            @Param("lifecycleStatus") BookingLifecycle lifecycleStatus,
            @Param("scheduleStatus") ScheduleStatus scheduleStatus,
            @Param("maintenanceStatus") MaintenanceStatus maintenanceStatus,
            @Param("paymentStatus") PaymentStatus paymentStatus
    );


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
    Page<Booking> findByScheduleStatus(ScheduleStatus status, Pageable pageable);
    Page<Booking> findByMaintenanceStatus(MaintenanceStatus status, Pageable pageable);
    Page<Booking> findByPaymentStatus(PaymentStatus status, Pageable pageable);
    Page<Booking> findByLifecycleStatus(BookingLifecycle status, Pageable pageable);
    Page<Booking> findByScheduleDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Custom: Find bookings by customer and date range
    @Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId AND b.scheduleDate BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsByCustomerAndDateRange(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.vehicle.vin = :vin AND b.scheduleDate BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsByVehicleAndDateRange(
            @Param("vin") String vin,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Count queries từng miền status
    Long countByScheduleStatus(ScheduleStatus status);
    Long countByMaintenanceStatus(MaintenanceStatus status);
    Long countByPaymentStatus(PaymentStatus status);
    Long countByLifecycleStatus(BookingLifecycle status);
    Long countByCustomerId(Long customerId);
    Long countByVehicleVin(String vin);

    // Revenue queries (doanh thu)
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.maintenanceStatus = :status")
    Double calculateTotalRevenueByMaintenanceStatus(@Param("status") MaintenanceStatus status);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.paymentStatus = :status")
    Double calculateTotalRevenueByPaymentStatus(@Param("status") PaymentStatus status);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.lifecycleStatus = :status")
    Double calculateTotalRevenueByLifecycleStatus(@Param("status") BookingLifecycle status);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.scheduleDate BETWEEN :startDate AND :endDate")
    Double calculateRevenueByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Statistics queries: booking count by từng loại status
    @Query("SELECT b.scheduleStatus, COUNT(b) FROM Booking b GROUP BY b.scheduleStatus")
    List<Object[]> getBookingCountByScheduleStatus();

    @Query("SELECT b.maintenanceStatus, COUNT(b) FROM Booking b GROUP BY b.maintenanceStatus")
    List<Object[]> getBookingCountByMaintenanceStatus();

    @Query("SELECT b.paymentStatus, COUNT(b) FROM Booking b GROUP BY b.paymentStatus")
    List<Object[]> getBookingCountByPaymentStatus();

    @Query("SELECT b.lifecycleStatus, COUNT(b) FROM Booking b GROUP BY b.lifecycleStatus")
    List<Object[]> getBookingCountByLifecycleStatus();

    @Query("SELECT DATE(b.scheduleDate), COUNT(b) FROM Booking b WHERE b.scheduleDate BETWEEN :startDate AND :endDate GROUP BY DATE(b.scheduleDate) ORDER BY DATE(b.scheduleDate)")
    List<Object[]> getBookingCountByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}

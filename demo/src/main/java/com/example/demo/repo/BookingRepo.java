package com.example.demo.repo;

import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.MaintenanceCatalogModelPart;
import com.example.demo.model.entity.Part;
import com.example.demo.model.modelEnum.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    // Basic finders
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByVehicleVin(String vin);

    // Find by từng status chính
    List<Booking> findByBookingStatus(BookingStatus status);

    // API lấy bookings theo filter chính (core)
    @Query("SELECT b FROM Booking b WHERE " +
            "(:bookingStatus IS NULL OR b.bookingStatus = :bookingStatus)")
    List<Booking> findWithFilters(
            @Param("bookingStatus") BookingStatus bookingStatus
    );

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE " +
            "CAST(b.scheduleDate AS DATE) = CAST(:scheduleDate AS DATE) AND " +
            "HOUR(b.scheduleDate) = HOUR(:scheduleDate) AND " +
            "b.bookingStatus != 'CANCELLED'")
    boolean isSlotBooked(@Param("scheduleDate") LocalDateTime scheduleDate);

    @Query("SELECT DISTINCT b.scheduleDate " +
            "FROM Booking b " +
            "WHERE b.scheduleDate >= :startTime " +
            "AND b.scheduleDate < :endTime " +
            "AND b.bookingStatus IN :statuses " +
            "AND EXTRACT(HOUR FROM b.scheduleDate) >= :startHour " +
            "AND EXTRACT(HOUR FROM b.scheduleDate) <= :endHour " +
            "ORDER BY b.scheduleDate ASC")// Sắp xếp để kết quả gọn gàng
    List<LocalDateTime> findBookedDateTimesInWorkingHours(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("statuses") List<BookingStatus> statuses,
            @Param("startHour") int startHour,
            @Param("endHour") int endHour
    );

    Booking findTopByCustomerIdAndVehicleVinAndBookingStatusOrderByScheduleDateDesc(
            Long customerId, String vin, BookingStatus bookingStatus
    );

    long countByBookingStatus(BookingStatus status);
    long countByBookingStatusNot(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.customer.id = :customerId AND CAST(b.scheduleDate AS DATE) = :searchDate")
    List<Booking> findByCustomerIdAndDate(
            @Param("customerId") Long customerId,
            @Param("searchDate") LocalDate searchDate
    );

    List<Booking> findByScheduleDate(LocalDateTime bookingDate);

    // Tìm các booking PAID, scheduleDate + additionalTime < now, chưa start
    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = 'PAID' AND b.scheduleDate <= :deadline")
    List<Booking> findPaidBookingsPastDeadline(@Param("deadline") LocalDateTime deadline);
}

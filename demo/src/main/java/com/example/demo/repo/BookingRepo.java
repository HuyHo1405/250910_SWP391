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

    Booking findTopByCustomerIdAndVehicleVinAndBookingStatusOrderByScheduleDateDesc(
            Long customerId, String vin, BookingStatus bookingStatus
    );
}

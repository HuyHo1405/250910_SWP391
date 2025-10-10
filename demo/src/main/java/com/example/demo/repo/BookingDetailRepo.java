package com.example.demo.repo;

import com.example.demo.model.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingDetailRepo extends JpaRepository<BookingDetail, Long> {
    // Basic finders
    List<BookingDetail> findByBookingId(Long bookingId);
    List<BookingDetail> findByServiceId(Long serviceId);
    Optional<BookingDetail> findByBookingIdAndServiceId(Long bookingId, Long serviceId);

    // Delete operations
    @Modifying
    @Query("DELETE FROM BookingDetail bd WHERE bd.booking.id = :bookingId")
    void deleteByBookingId(@Param("bookingId") Long bookingId);

    @Modifying
    @Query("DELETE FROM BookingDetail bd WHERE bd.booking.id = :bookingId AND bd.service.id = :serviceId")
    void deleteByBookingIdAndServiceId(@Param("bookingId") Long bookingId, @Param("serviceId") Long serviceId);

    // Calculate totals
    @Query("SELECT SUM(bd.servicePrice) FROM BookingDetail bd WHERE bd.booking.id = :bookingId")
    Double calculateTotalByBookingId(@Param("bookingId") Long bookingId);

    // Count operations
    Long countByBookingId(Long bookingId);
    Long countByServiceId(Long serviceId);

    // Statistics queries
    @Query("SELECT bd.service.name, COUNT(bd) FROM BookingDetail bd GROUP BY bd.service.name ORDER BY COUNT(bd) DESC")
    List<Object[]> getMostPopularServices();

    @Query("SELECT bd.service.name, SUM(bd.servicePrice) FROM BookingDetail bd " +
            "GROUP BY bd.service.name ORDER BY SUM(bd.servicePrice) DESC")
    List<Object[]> getServiceRevenueStatistics();

    // Find booking details with service information
    @Query("SELECT bd FROM BookingDetail bd JOIN FETCH bd.service WHERE bd.booking.id = :bookingId")
    List<BookingDetail> findByBookingIdWithService(@Param("bookingId") Long bookingId);

    // Find booking details by price range
    @Query("SELECT bd FROM BookingDetail bd WHERE bd.servicePrice BETWEEN :minPrice AND :maxPrice")
    List<BookingDetail> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
}

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
    List<BookingDetail> findByCatalogId(Long catalogId);
    Optional<BookingDetail> findByBookingIdAndCatalogId(Long bookingId, Long catalogId);

    // Delete operations
    @Modifying
    @Query("DELETE FROM BookingDetail bd WHERE bd.booking.id = :bookingId")
    void deleteByBookingId(@Param("bookingId") Long bookingId);

    @Modifying
    @Query("DELETE FROM BookingDetail bd WHERE bd.booking.id = :bookingId AND bd.catalog.id = :serviceId")
    void deleteByBookingIdAndServiceId(@Param("bookingId") Long bookingId, @Param("serviceId") Long serviceId);

    // Statistics queries
    @Query("SELECT bd.catalog.name, COUNT(bd) FROM BookingDetail bd GROUP BY bd.catalog.name ORDER BY COUNT(bd) DESC")
    List<Object[]> getMostPopularServices();
}

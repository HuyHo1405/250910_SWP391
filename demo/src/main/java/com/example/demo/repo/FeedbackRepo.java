package com.example.demo.repo;

import com.example.demo.model.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByBookingId(Long bookingId);

    @Query("SELECT DISTINCT f FROM Feedback f " +
            "LEFT JOIN f.feedbackTags t " +  // Join để lọc theo Tag nếu cần
            "WHERE " +
            "(:bookingId IS NULL OR f.booking.id = :bookingId) AND " +
            "(:customerId IS NULL OR f.customer.id = :customerId) AND " +
            "(:rating IS NULL OR f.rating = :rating) AND " +
            "(:tagId IS NULL OR t.id = :tagId)")
    List<Feedback> findWithFilters(
            @Param("bookingId") Long bookingId,
            @Param("customerId") Long customerId,
            @Param("rating") Integer rating,
            @Param("tagId") Long tagId
    );

}


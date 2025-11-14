package com.example.demo.repo;

import com.example.demo.model.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepo extends JpaRepository<Job, Long> {
    /**
     * Tìm Job duy nhất của Booking (One-to-One relationship)
     */
    Optional<Job> findByBookingId(Long bookingId);

    List<Job> findByTechnicianIsNotNullAndActualEndTimeIsNotNullAndEstEndTimeIsNotNull();

    @Query("SELECT j FROM Job j " +
            "WHERE j.technician.id = :technicianId " +
            "AND j.actualEndTime IS NULL")
    List<Job> findByTechnicianIdAndNotComplete(@Param("technicianId") Long technicianId);

    @Query("SELECT j FROM Job j " +
            "WHERE j.technician IS NULL")
    List<Job> findUnassignJob();

    /**
     * Kiểm tra booking có job không
     */
    boolean existsByBookingId(@Param("bookingId") Long bookingId);
}

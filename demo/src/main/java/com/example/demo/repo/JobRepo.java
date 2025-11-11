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
    Optional<Job> findByBookingDetailId(Long bookingDetailId);

    @Query("SELECT j FROM Job j " +
            "WHERE j.technician.id = :technicianId " +
            "AND j.actualEndTime IS NULL")
    List<Job> findByTechnicianIdAndNotComplete(@Param("technicianId") Long technicianId);

    @Query("SELECT j FROM Job j " +
            "WHERE j.technician IS NULL")
    List<Job> findUnassignJob();

    /**
     * Lấy tất cả jobs của một booking
     */
    @Query("SELECT j FROM Job j " +
            "WHERE j.bookingDetail.booking.id = :bookingId")
    List<Job> findByBookingId(@Param("bookingId") Long bookingId);

    /**
     * Kiểm tra booking có jobs không
     */
    @Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END FROM Job j " +
            "WHERE j.bookingDetail.booking.id = :bookingId")
    boolean existsByBookingId(@Param("bookingId") Long bookingId);
}

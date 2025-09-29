package com.example.demo.repo;

import com.example.demo.model.entity.User;
import com.example.demo.model.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepo extends JpaRepository<VerificationCode, Long> {
    /**
     * Find the latest active verification code for a user
     * @param user The user to find the code for
     * @return Optional containing the verification code if found
     */
    Optional<VerificationCode> findFirstByUserAndUsedFalseOrderByCreatedAtDesc(User user);

    /**
     * Find a verification code by user and code value
     * @param user The user who owns the code
     * @param code The code value to find
     * @param now Current timestamp to check code expiration
     * @return Optional containing the verification code if found and valid
     */
    @Query("SELECT v FROM VerificationCode v " +
           "WHERE v.user = :user " +
           "AND v.code = :code " +
           "AND v.used = false " +
           "AND v.expiresAt > :now")
    Optional<VerificationCode> findValidVerificationCode(
            @Param("user") User user,
            @Param("code") String code,
            @Param("now") LocalDateTime now
    );

    /**
     * Invalidate all unused verification codes for a user
     * @param userId The ID of the user
     * @return Number of codes invalidated
     */
    @Modifying
    @Query("UPDATE VerificationCode v SET v.used = true " +
           "WHERE v.user.id = :userId AND v.used = false")
    int invalidateUserCodes(@Param("userId") Long userId);

    /**
     * Delete expired verification codes
     * @param expiryTime The time before which codes are considered expired
     * @return Number of codes deleted
     */
    @Modifying
    @Query("DELETE FROM VerificationCode v WHERE v.expiresAt < :expiryTime")
    int deleteExpiredCodes(@Param("expiryTime") LocalDateTime expiryTime);
}

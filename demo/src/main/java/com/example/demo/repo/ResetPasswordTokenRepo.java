package com.example.demo.repo;

import com.example.demo.model.entity.ResetPasswordToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ResetPasswordTokenRepo extends JpaRepository<ResetPasswordToken, Long> {
    // Find a token by its string value
    Optional<ResetPasswordToken> findByToken(String token);

    // Find a valid, non-revoked token by user ID
    @Query("SELECT t FROM ResetPasswordToken t WHERE t.userId = :userId AND t.revoked = false AND t.expiryDate > :now")
    Optional<ResetPasswordToken> findValidTokenByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Revoke all tokens for a specific user
    @Modifying
    @Transactional
    @Query("UPDATE ResetPasswordToken t SET t.revoked = true WHERE t.userId = :userId")
    void revokeUserTokens(@Param("userId") Long userId);

    // Delete all expired tokens
    @Modifying
    @Transactional
    @Query("DELETE FROM ResetPasswordToken t WHERE t.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    // Check if a valid token exists for a user
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM ResetPasswordToken t " +
            "WHERE t.userId = :userId AND t.revoked = false AND t.expiryDate > :now")
    boolean existsValidTokenForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}

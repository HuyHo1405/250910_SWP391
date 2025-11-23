package com.example.demo.repo;

import com.example.demo.model.entity.Payment;
import com.example.demo.model.modelEnum.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionRef(@NotBlank(message = "Mã giao dịch không được để trống") @Size(max = 255, message = "Mã giao dịch không được vượt quá 255 ký tự") String transactionRef);

    Optional<Payment> findByInvoiceId(Long invoiceId);

    Optional<Payment> findByOrderCode(String orderCode);

    @Query("SELECT SUM(p.amount) FROM Payment p " +
            "WHERE p.responseCode = '00' " +
            "AND p.paidAt >= :startDate AND p.paidAt < :endDate")
    BigDecimal sumSuccessfulRevenueBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);


    @Query("SELECT p FROM Payment p WHERE p.invoice.booking.id = :bookingId ORDER BY p.createdAt DESC")
    List<Payment> findByBookingId(@Param("bookingId") Long bookingId);

    Optional<Payment> findFirstByInvoiceIdOrderByCreatedAtDesc(Long invoiceId);

    List<Payment> findByInvoiceIdAndStatus(Long invoiceId, PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.invoice.booking.customer.id = :customerId ORDER BY p.createdAt DESC")
    List<Payment> findByCustomerId(@Param("customerId") Long customerId);
}
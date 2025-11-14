package com.example.demo.repo;

import com.example.demo.model.entity.Payment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionRef(@NotBlank(message = "Mã giao dịch không được để trống") @Size(max = 255, message = "Mã giao dịch không được vượt quá 255 ký tự") String transactionRef);

    Optional<Payment> findByInvoiceId(Long invoiceId);

    @Query("SELECT p FROM Payment p WHERE p.invoice.booking.id = :bookingId ORDER BY p.createdAt DESC")
    List<Payment> findByBookingId(@Param("bookingId") Long bookingId);

    Optional<Payment> findByOrderCode(String orderCode);
}

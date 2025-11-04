package com.example.demo.repo;

import com.example.demo.model.entity.Payment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionRef(@NotBlank(message = "Mã giao dịch không được để trống") @Size(max = 255, message = "Mã giao dịch không được vượt quá 255 ký tự") String transactionRef);

    Optional<Payment> findByInvoiceId(Long invoiceId);

    Optional<Payment> findByOrderCode(String orderCode);
}

package com.example.demo.repo;

import com.example.demo.model.entity.Invoice;
import com.example.demo.model.entity.InvoiceLine;
import com.example.demo.model.modelEnum.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepo extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByBookingId(Long bookingId);
    List<Invoice> findByStatusAndDueDateBefore(InvoiceStatus status, LocalDateTime now);
}

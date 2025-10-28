package com.example.demo.repo;

import com.example.demo.model.entity.Invoice;
import com.example.demo.model.entity.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepo extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByBookingId(Long bookingId);

}

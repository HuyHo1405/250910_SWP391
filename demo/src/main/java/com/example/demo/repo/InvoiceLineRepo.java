package com.example.demo.repo;

import com.example.demo.model.entity.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceLineRepo extends JpaRepository<InvoiceLine, Long> {
}

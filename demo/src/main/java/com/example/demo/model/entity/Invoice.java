package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Invoice {

    // ================================
    // COLUMNS - Database Fields
    // ================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "tax_amount", nullable = false)
    private Double taxAmount;

    @Column(name = "discount_amount")
    private Double discountAmount;

    @Column(name = "final_amount", nullable = false)
    private Double finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private InvoiceStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ================================
    // RELATIONSHIPS
    // ================================

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    // ================================
    // PRE/POST FUNCTIONS - Lifecycle Callbacks
    // ================================

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        // Generate invoice number if not set
        if (this.invoiceNumber == null) {
            this.invoiceNumber = generateInvoiceNumber();
        }

        // Set default status if not set
        if (this.status == null) {
            this.status = InvoiceStatus.DRAFT;
        }

        // Set default discount amount if not set
        if (this.discountAmount == null) {
            this.discountAmount = 0.0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ================================
    // UTILITY METHODS
    // ================================

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }

    public void calculateFinalAmount() {
        this.finalAmount = this.totalAmount + this.taxAmount - this.discountAmount;
    }

    // Helper methods for managing payments
    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setInvoice(this);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
        payment.setInvoice(null);
    }
}

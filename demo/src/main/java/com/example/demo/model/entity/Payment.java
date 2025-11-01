package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.PaymentMethod;
import com.example.demo.model.modelEnum.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Payment {

    // ================================
    // COLUMNS - Database Fields
    // ================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "amount", nullable = false)
    private Double amount; // Lưu ý: Dùng BigDecimal sẽ chính xác hơn cho tiền tệ

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    // --- BỔ SUNG 1: Mã đơn hàng của BẠN ---
    // Mã này bạn tạo ra để gửi cho PayOS
    @Column(name = "order_code", length = 100, unique = true, nullable = false)
    private String orderCode;

    // --- BỔ SUNG 2: Dữ liệu thô từ Webhook ---
    @Column(name = "raw_response_data", columnDefinition = "NVARCHAR(MAX)")
    private String rawResponseData;

    @Column(name = "transaction_ref", length = 255)
    private String transactionRef; // Đây là mã của PayOS trả về

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ================================
    // LIFECYCLE METHODS
    // ================================

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
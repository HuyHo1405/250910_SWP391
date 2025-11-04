package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.PaymentMethod;
import com.example.demo.model.modelEnum.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal; // <-- THAY ĐỔI 1: Import BigDecimal
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

    // --- THAY ĐỔI 2: Dùng BigDecimal cho tiền tệ ---
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount; // Đã đổi từ Double

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    // Mã đơn hàng của BẠN (dùng để gửi đi, vd: vnp_TxnRef)
    @Column(name = "order_code", length = 100, unique = true, nullable = false)
    private String orderCode;

    // Mã giao dịch của VNPAY (vd: vnp_TransactionNo)
    @Column(name = "transaction_ref", length = 255)
    private String transactionRef;

    // --- THAY ĐỔI 3: Thêm trường lưu mã phản hồi ---
    // Sẽ lưu '00' (thành công) hoặc '09', '21' (lỗi)
    @Column(name = "response_code", length = 10)
    private String responseCode;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // Dữ liệu thô từ IPN (để debug)
    @Column(name = "raw_response_data", columnDefinition = "NVARCHAR(MAX)")
    private String rawResponseData;

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
        this.updatedAt =LocalDateTime.now();
    }
}
package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vin", referencedColumnName = "vin")
    private Vehicle vehicle;

    @Column(name = "schedule_date", nullable = false)
    private LocalDateTime scheduleDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_status", nullable = false, length = 20)
    private ScheduleStatus scheduleStatus;  // TRẠNG THÁI LỊCH HẸN

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_status", nullable = false, length = 30)
    private MaintenanceStatus maintenanceStatus;  // TRẠNG THÁI BẢO DƯỠNG

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;    // TRẠNG THÁI THANH TOÁN

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_status", nullable = false, length = 20)
    private BookingLifecycle lifecycleStatus;  // TRẠNG THÁI ĐƠN TỔNG THỂ

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookingDetail> bookingDetails = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Khởi tạo mặc định các status
        if (scheduleStatus == null) {
            scheduleStatus = ScheduleStatus.PENDING;
        }
        if (maintenanceStatus == null) {
            maintenanceStatus = MaintenanceStatus.IDLE;
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.UNPAID;
        }
        if (lifecycleStatus == null) {
            lifecycleStatus = BookingLifecycle.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods for bidirectional relationship
    public void addBookingDetail(BookingDetail detail) {
        bookingDetails.add(detail);
        detail.setBooking(this);
    }

    public void removeBookingDetail(BookingDetail detail) {
        bookingDetails.remove(detail);
        detail.setBooking(null);
    }
}

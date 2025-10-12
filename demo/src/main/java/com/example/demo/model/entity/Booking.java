package com.example.demo.model.entity;

import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.model.modelEnum.PaymentStatus;
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
    @Column(nullable = false, length = 50)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;

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
        if (status == null) {
            status = BookingStatus.PENDING;
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.UNPAID;
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
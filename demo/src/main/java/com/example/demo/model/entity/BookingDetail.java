package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "booking_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_catalog_id", nullable = false)
    private MaintenanceCatalog catalog;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String description;
}

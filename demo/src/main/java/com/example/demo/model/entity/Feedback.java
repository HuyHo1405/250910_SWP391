package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "feedbacks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating; // Số sao (1-5), validation sẽ làm ở DTO hoặc @PrePersist

    @Column(columnDefinition = "NVARCHAR(100)") // Cho phép nhập nội dung dài
    private String comment;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "feedback_tag_mapping",
            joinColumns = @JoinColumn(name = "feedback_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<FeedbackTag> feedbackTags = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        validateRating();
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        validateRating();
    }

    public void validateRating() {
        if (rating != null) {
            if (rating < 1) rating = 1;
            if (rating > 5) rating = 5;
        }
    }

}

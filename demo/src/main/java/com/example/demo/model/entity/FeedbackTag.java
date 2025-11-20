package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedback_tags")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FeedbackTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(70)")
    private String content; // Ví dụ: "Xe chạy êm", "Thợ nhiệt tình"

    @Column(name = "target_rating", nullable = false)
    private Integer targetRating; // Mức đánh giá mục tiêu (1-5), có thể null nếu không áp dụng
}

package com.example.demo.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class FeedbackResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private Set<TagResponse> tags; // Trả về cả tên tag để hiển thị
    private Long bookingId;
    private Long customerId;
    private String customerName; // Chỉ trả về tên khách, không trả cả object User nhạy cảm
    private LocalDateTime createdAt;
}
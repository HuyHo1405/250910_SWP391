package com.example.demo.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;

@Data
public class FeedbackRequest {

    @NotNull(message = "Booking ID không được để trống")
    private Long bookingId;

    @NotNull(message = "Vui lòng chọn số sao")
    @Min(value = 1, message = "Đánh giá thấp nhất là 1 sao")
    @Max(value = 5, message = "Đánh giá cao nhất là 5 sao")
    private Integer rating;

    private String comment;

    // Danh sách ID của các tag mà khách chọn (VD: [1, 3, 5])
    private Set<Long> tagIds;
}
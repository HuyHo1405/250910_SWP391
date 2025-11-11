package com.example.demo.model.dto;

import com.example.demo.model.modelEnum.JobStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {
    private Long id;
    private String description;
    private Long bookingDetailId;
    private Long technicianId;
    private String technicianName;    // Tên kỹ thuật viên
    private LocalDateTime startTime;
    private LocalDateTime estEndTime;
    private LocalDateTime actualEndTime;
    private JobStatus status;         // ← ĐỔI: String → JobStatus enum
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

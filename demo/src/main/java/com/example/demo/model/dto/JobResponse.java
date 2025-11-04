package com.example.demo.model.dto;

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
    private Long technicianId;
    private String technicianName;    // Tên kỹ thuật viên
    private LocalDateTime startTime;
    private LocalDateTime estEndTime;
    private LocalDateTime actualEndTime;
    private String status;            // PENDING / IN_PROGRESS / COMPLETED
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}



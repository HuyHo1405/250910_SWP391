package com.example.demo.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReminderInfo {
    private double lastOdometer;
    private LocalDateTime lastDate;
}

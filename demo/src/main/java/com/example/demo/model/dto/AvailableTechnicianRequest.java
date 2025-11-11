package com.example.demo.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTechnicianRequest {

    @Valid
    @NotNull(message = "Thời gian hẹn không được để trống")
    private ScheduleDateTime scheduleDateTime;
}


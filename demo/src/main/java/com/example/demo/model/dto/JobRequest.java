package com.example.demo.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class JobRequest {

    @Data
    public static class JobAssign{
        private Long bookingDetailId;
        private Long technicianId;
        private String notes;
    }

    @Data
    public static class JobUpdate{
        private Long bookingDetailId;
        private Long technicianId;
        private String notes;
    }

}

package com.example.demo.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class ScheduleDateTime {
    @NotBlank(message = "Định dạng ngày/giờ không được để trống")
    private String format;          // Ex: "yyyy-MM-dd HH:mm", "dd/MM/yyyy HH:mm", "timestamp", "iso"
    @NotBlank(message = "Giá trị ngày/giờ không được để trống")
    private String value;           // Ex: "2025-10-14 13:30", "14/10/2025 13:30", ISO string, or timestamp

    // You can add a timezone field if needed:
    private String timezone;        // Optional

    @AssertTrue(message = "Định dạng hoặc giá trị ngày/giờ không hợp lệ")
    public boolean isValid() {
        try {
            if (format == null || value == null) return false;
            switch (format) {
                case "yyyy-MM-dd HH:mm:ss":
                    LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    return true;
                case "timestamp":
                    Long val = Long.parseLong(value);
                    Instant.ofEpochMilli(val).atZone(timezone != null ? ZoneId.of(timezone) : ZoneId.of("UTC"));
                    return true;
                case "iso":
                    LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
                    return true;
                default:
                    return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

}

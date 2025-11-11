package com.example.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
@Builder
public class ScheduleDateTime {
    @NotBlank(message = "Định dạng ngày/giờ không được để trống")
    private String format;          // Ex: "yyyy-MM-dd HH:mm:ss", "timestamp", "iso"

    @NotBlank(message = "Giá trị ngày/giờ không được để trống")
    private String value;           // Ex: "2025-10-14 13:00:00", "1699704600000", "2025-11-11T14:00:00"

    private String timezone;        // Optional, default: "UTC"

    @JsonIgnore
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

    /**
     * Kiểm tra xem giờ hẹn có phải giờ tròn không (phút = 0, giây = 0)
     * VD: 09:00:00 ✅, 09:30:00 ❌, 09:00:15 ❌
     */
    @JsonIgnore
    @AssertTrue(message = "Giờ hẹn chỉ được chọn theo giờ tròn (phút và giây phải là 00)")
    public boolean isHourOnly() {
        try {
            if (!isValid()) return false;

            LocalDateTime dateTime = toLocalDateTime();

            // Kiểm tra phút = 0 và giây = 0
            return dateTime.getMinute() == 0 && dateTime.getSecond() == 0;

        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Convert sang LocalDateTime để sử dụng trong business logic
     * @return LocalDateTime đã parse
     * @throws IllegalArgumentException nếu format không hợp lệ
     */
    @JsonIgnore
    public LocalDateTime toLocalDateTime() {
        if (format == null || value == null) {
            throw new IllegalArgumentException("Format and value must not be null");
        }

        try {
            switch (format) {
                case "yyyy-MM-dd HH:mm:ss":
                    return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                case "timestamp":
                    Long val = Long.parseLong(value);
                    ZoneId zone = timezone != null ? ZoneId.of(timezone) : ZoneId.of("UTC");
                    return Instant.ofEpochMilli(val).atZone(zone).toLocalDateTime();

                case "iso":
                    return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);

                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (DateTimeParseException | NumberFormatException ex) {
            throw new IllegalArgumentException("Failed to parse datetime: " + ex.getMessage(), ex);
        }
    }
}

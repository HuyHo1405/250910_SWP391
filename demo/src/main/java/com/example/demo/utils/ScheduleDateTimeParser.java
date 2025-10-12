package com.example.demo.utils;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.ScheduleDateTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ScheduleDateTimeParser {
    /**
     * Parses a ScheduleDateTime DTO to a LocalDateTime, supporting multiple formats.
     *
     * @param dto The incoming schedule object (from request)
     * @return LocalDateTime (ready for business/entity use)
     * @throws IllegalArgumentException if format is unsupported or value is invalid
     */
    public static LocalDateTime parse(ScheduleDateTime dto) {
        if (dto == null || dto.getFormat() == null || dto.getValue() == null) {
            throw new IllegalArgumentException("Missing schedule date/time or format.");
        }

        try {
            switch (dto.getFormat()) {
                case "yyyy-MM-dd HH:mm:ss":
                    DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    return LocalDateTime.parse(dto.getValue(), dtFormat);
                case "iso":
                    return LocalDateTime.parse(dto.getValue(), DateTimeFormatter.ISO_DATE_TIME);
                case "timestamp":
                    long millis = Long.parseLong(dto.getValue());
                    String tz = dto.getTimezone();
                    ZoneId zone = (tz == null || tz.isBlank()) ? ZoneId.of("UTC") : ZoneId.of(tz);
                    return Instant.ofEpochMilli(millis).atZone(zone).toLocalDateTime();
                default:
                    throw new CommonException.BadRequest("Unsupported date/time format: " + dto.getFormat());
            }
        } catch (Exception ex) {
            throw new CommonException.BadRequest("Invalid schedule date/time value: " + ex.getMessage());
        }
    }

    // Format LocalDateTime (entity) thành ScheduleDateTime DTO cho response
    public static ScheduleDateTime format(LocalDateTime date, String format, String timezone) {
        if (date == null) return null;
        String value;
        switch (format) {
            case "yyyy-MM-dd HH:mm:ss":
                value = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                break;
            case "iso":
                value = date.format(DateTimeFormatter.ISO_DATE_TIME);
                break;
            case "timestamp":
                value = String.valueOf(date.atZone(timezone != null ? ZoneId.of(timezone) : ZoneId.of("UTC"))
                        .toInstant().toEpochMilli());
                break;
            default:
                value = date.toString();
        }
        return ScheduleDateTime.builder().format(format).value(value).timezone(timezone).build();
    }
}

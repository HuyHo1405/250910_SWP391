package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class JobException extends BaseServiceException {
    public JobException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // ================================
    // GENERAL
    // ================================

    public static class NotFound extends JobException {
        public NotFound() {
            super("JOB_NOT_FOUND", "Không tìm thấy công việc này", HttpStatus.NOT_FOUND);
        }
    }

    public static class AlreadyExists extends JobException {
        public AlreadyExists() {
            super("JOB_ALREADY_EXISTS", "Công việc này đã tồn tại cho BookingDetail", HttpStatus.CONFLICT);
        }
    }

    // ================================
    // STATE & OPERATION
    // ================================

    public static class BookingNotInProgress extends JobException {
        public BookingNotInProgress() {
            super("BOOKING_NOT_IN_PROGRESS", "Không thể gán việc khi booking chưa ở trạng thái IN_PROGRESS", HttpStatus.BAD_REQUEST);
        }
    }

    public static class JobAlreadyStarted extends JobException {
        public JobAlreadyStarted() {
            super("JOB_ALREADY_STARTED", "Không thể thực hiện. Công việc đã được bắt đầu", HttpStatus.CONFLICT);
        }
    }

    public static class JobNotStarted extends JobException {
        public JobNotStarted() {
            super("JOB_NOT_STARTED", "Không thể hoàn thành. Công việc chưa được bắt đầu", HttpStatus.CONFLICT);
        }
    }

    public static class JobAlreadyCompleted extends JobException {
        public JobAlreadyCompleted() {
            super("JOB_ALREADY_COMPLETED", "Công việc này đã được hoàn thành trước đó", HttpStatus.CONFLICT);
        }
    }
}
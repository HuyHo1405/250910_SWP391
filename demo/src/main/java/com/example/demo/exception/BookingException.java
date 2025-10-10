package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class BookingException extends BaseServiceException{
    public BookingException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // ================================
    // STATUS MANAGEMENT
    // ================================

    public static class InvalidStatusTransition extends BookingException {
        public InvalidStatusTransition(String from, String to) {
            super(
                    "INVALID_STATUS_TRANSITION",
                    String.format("Cannot transition from %s to %s", from, to),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class CannotDelete extends BookingException {
        public CannotDelete(String status) {
            super(
                    "CANNOT_DELETE_BOOKING",
                    String.format("Cannot delete booking with status: %s", status),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // ================================
    // SCHEDULING
    // ================================

    public static class ScheduleConflict extends BookingException {
        public ScheduleConflict() {
            super(
                    "SCHEDULE_CONFLICT",
                    "This time slot is already booked",
                    HttpStatus.CONFLICT
            );
        }
    }

    public static class SchedulePassed extends BookingException {
        public SchedulePassed() {
            super(
                    "SCHEDULE_PASSED",
                    "Schedule date must be in the future",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class InvalidDateRange extends BookingException {
        public InvalidDateRange() {
            super(
                    "INVALID_DATE_RANGE",
                    "Start date must be before end date",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // ================================
    // BUSINESS RULES
    // ================================

    public static class ServiceInactive extends BookingException {
        public ServiceInactive(String serviceName) {
            super(
                    "SERVICE_INACTIVE",
                    String.format("Service '%s' is not available", serviceName),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class VehicleNotOwned extends BookingException {
        public VehicleNotOwned() {
            super(
                    "VEHICLE_NOT_OWNED",
                    "Vehicle does not belong to this customer",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class EmptyServiceList extends BookingException {
        public EmptyServiceList() {
            super(
                    "EMPTY_SERVICE_LIST",
                    "Booking must have at least one service",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}

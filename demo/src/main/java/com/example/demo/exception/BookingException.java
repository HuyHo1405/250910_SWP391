package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class BookingException extends BaseServiceException {

    public BookingException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // ================================
    // STATUS MANAGEMENT (all domains)
    // ================================

    public static class InvalidStatusTransition extends BookingException {
        public InvalidStatusTransition(String domain, String from, String to) {
            super(
                    "INVALID_STATUS_TRANSITION",
                    String.format("[%s] Cannot transition from %s to %s", domain, from, to),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class CannotDelete extends BookingException {
        public CannotDelete(String lifecycleStatus) {
            super(
                    "CANNOT_DELETE_BOOKING",
                    String.format("Cannot delete booking with lifecycle status: %s", lifecycleStatus),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class CannotCancel extends BookingException {
        public CannotCancel(String status, String reason) {
            super(
                    "CANNOT_CANCEL_BOOKING",
                    String.format("Cannot cancel: status=%s, reason=%s", status, reason),
                    HttpStatus.CONFLICT
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

    public static class AlreadyCheckedIn extends BookingException {
        public AlreadyCheckedIn() {
            super(
                    "ALREADY_CHECKED_IN",
                    "The booking has already been checked in",
                    HttpStatus.CONFLICT
            );
        }
    }

    // ================================
    // MAINTENANCE
    // ================================

    public static class CannotStartInspection extends BookingException {
        public CannotStartInspection(String scheduleStatus) {
            super(
                    "CANNOT_START_INSPECTION",
                    String.format("Cannot start inspection when schedule status is: %s", scheduleStatus),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class MaintenanceNotApproved extends BookingException {
        public MaintenanceNotApproved() {
            super(
                    "MAINTENANCE_NOT_APPROVED",
                    "Repair/maintenance must be approved before starting",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class MaintenanceAlreadyCompleted extends BookingException {
        public MaintenanceAlreadyCompleted() {
            super(
                    "MAINTENANCE_ALREADY_COMPLETED",
                    "Maintenance is already completed",
                    HttpStatus.CONFLICT
            );
        }
    }

    // ================================
    // PAYMENT
    // ================================

    public static class PaymentAlreadyMade extends BookingException {
        public PaymentAlreadyMade() {
            super(
                    "PAYMENT_ALREADY_MADE",
                    "Payment has already been processed",
                    HttpStatus.CONFLICT
            );
        }
    }

    public static class PaymentNotAuthorized extends BookingException {
        public PaymentNotAuthorized() {
            super(
                    "PAYMENT_NOT_AUTHORIZED",
                    "Payment is not authorized/pre-authorized",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class RefundNotAllowed extends BookingException {
        public RefundNotAllowed(String reason) {
            super(
                    "REFUND_NOT_ALLOWED",
                    String.format("Refund not allowed. Reason: %s", reason),
                    HttpStatus.CONFLICT
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

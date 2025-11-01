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
                    String.format("[%s] Không thể chuyển trạng thái từ %s sang %s", domain, from, to),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class CannotDelete extends BookingException {
        public CannotDelete(String lifecycleStatus) {
            super(
                    "CANNOT_DELETE_BOOKING",
                    String.format("Không thể xóa đặt lịch với trạng thái vòng đời: %s", lifecycleStatus),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class CannotCancel extends BookingException {
        public CannotCancel(String status, String reason) {
            super(
                    "CANNOT_CANCEL_BOOKING",
                    String.format("Không thể hủy: trạng thái=%s, lý do=%s", status, reason),
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
                    "Khung thời gian này đã được đặt lịch",
                    HttpStatus.CONFLICT
            );
        }
    }

    public static class SchedulePassed extends BookingException {
        public SchedulePassed() {
            super(
                    "SCHEDULE_PASSED",
                    "Ngày đặt lịch phải trong tương lai",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class InvalidDateRange extends BookingException {
        public InvalidDateRange() {
            super(
                    "INVALID_DATE_RANGE",
                    "Ngày bắt đầu phải trước ngày kết thúc",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class AlreadyCheckedIn extends BookingException {
        public AlreadyCheckedIn() {
            super(
                    "ALREADY_CHECKED_IN",
                    "Đặt lịch đã được nhận xe",
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
                    String.format("Không thể bắt đầu kiểm tra khi trạng thái đặt lịch là: %s", scheduleStatus),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class MaintenanceNotApproved extends BookingException {
        public MaintenanceNotApproved() {
            super(
                    "MAINTENANCE_NOT_APPROVED",
                    "Sửa chữa/bảo trì phải được phê duyệt trước khi bắt đầu",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class MaintenanceAlreadyCompleted extends BookingException {
        public MaintenanceAlreadyCompleted() {
            super(
                    "MAINTENANCE_ALREADY_COMPLETED",
                    "Bảo trì đã hoàn thành",
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
                    "Thanh toán đã được xử lý",
                    HttpStatus.CONFLICT
            );
        }
    }

    public static class PaymentNotAuthorized extends BookingException {
        public PaymentNotAuthorized() {
            super(
                    "PAYMENT_NOT_AUTHORIZED",
                    "Thanh toán chưa được phê duyệt/ủy quyền trước",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class RefundNotAllowed extends BookingException {
        public RefundNotAllowed(String reason) {
            super(
                    "REFUND_NOT_ALLOWED",
                    String.format("Không được phép hoàn tiền. Lý do: %s", reason),
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
                    String.format("Dịch vụ '%s' không có sẵn", serviceName),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class VehicleNotOwned extends BookingException {
        public VehicleNotOwned() {
            super(
                    "VEHICLE_NOT_OWNED",
                    "Xe không thuộc về khách hàng này",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class EmptyServiceList extends BookingException {
        public EmptyServiceList() {
            super(
                    "EMPTY_SERVICE_LIST",
                    "Đặt lịch phải có ít nhất một dịch vụ",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public static class ServiceNotCompatibleWithVehicle extends BookingException {
        public ServiceNotCompatibleWithVehicle(Long serviceId, String vin) {
            super(
                    "SERVICE_NOT_COMPATIBLE_WITH_VEHICLE",
                    "Dịch vụ ID " + serviceId + " không áp dụng cho xe VIN " + vin,
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}

package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class VehicleException extends BaseServiceException {

    protected VehicleException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // ================================
    // BUSINESS RULES
    // ================================

    public static class ModelInUse extends VehicleException {
        public ModelInUse(Long modelId) {
            super(
                    "MODEL_IN_USE",
                    String.format("Không thể xóa mẫu xe %d - đang được sử dụng bởi các xe khác", modelId),
                    HttpStatus.CONFLICT
            );
        }
    }

    public static class InvalidOwnership extends VehicleException {
        public InvalidOwnership() {
            super(
                    "INVALID_OWNERSHIP",
                    "Bạn không sở hữu xe này",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    public static class InvalidVehicleStatus extends VehicleException {
        public InvalidVehicleStatus(String status) {
            super(
                    "INVALID_VEHICLE_STATUS",
                    String.format("Không thể thao tác với xe có trạng thái: %s", status),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
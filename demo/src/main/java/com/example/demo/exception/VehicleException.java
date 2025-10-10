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
                    String.format("Cannot delete vehicle model %d - it is in use by vehicles", modelId),
                    HttpStatus.CONFLICT
            );
        }
    }

    public static class InvalidOwnership extends VehicleException {
        public InvalidOwnership() {
            super(
                    "INVALID_OWNERSHIP",
                    "You don't own this vehicle",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    public static class InvalidVehicleStatus extends VehicleException {
        public InvalidVehicleStatus(String status) {
            super(
                    "INVALID_VEHICLE_STATUS",
                    String.format("Cannot perform operation on vehicle with status: %s", status),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
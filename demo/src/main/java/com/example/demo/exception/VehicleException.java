package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class VehicleException extends BaseServiceException {

    protected VehicleException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // --- SPECIFIC EXCEPTIONS ---

    public static class VehicleAlreadyExists extends VehicleException {
        public VehicleAlreadyExists(String vin) {
            super("VEHICLE_EXISTS",
                    "A vehicle with VIN '" + vin + "' already exists.",
                    HttpStatus.CONFLICT);
        }
    }

    public static class DuplicatePlateNumber extends VehicleException {
        public DuplicatePlateNumber(String plateNumber) {
            super("DUPLICATE_PLATE_NUMBER",
                    "The plate number '" + plateNumber + "' is already in use.",
                    HttpStatus.CONFLICT);
        }
    }

    public static class UnauthorizedAccess extends VehicleException {
        public UnauthorizedAccess() {
            super("UNAUTHORIZED_ACCESS",
                    "You are not authorized to perform this action.",
                    HttpStatus.FORBIDDEN);
        }
    }

    public static class VehicleNotFound extends VehicleException {
        public VehicleNotFound(String vin) {
            super("VEHICLE_NOT_FOUND",
                    "Vehicle not found with VIN '" + vin + "'.",
                    HttpStatus.NOT_FOUND);
        }
    }

    public static class InvalidVehicleModel extends VehicleException {
        public InvalidVehicleModel(Long modelId) {
            super("INVALID_VEHICLE_MODEL",
                    "Vehicle model not found with ID '" + modelId + "'.",
                    HttpStatus.BAD_REQUEST);
        }
    }
}

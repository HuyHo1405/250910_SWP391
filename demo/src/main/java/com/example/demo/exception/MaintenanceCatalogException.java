package com.example.demo.exception;

import org.springframework.http.HttpStatus;

public class MaintenanceCatalogException extends BaseServiceException {
    protected MaintenanceCatalogException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    public static class ServiceInactive extends MaintenanceCatalogException {
        public ServiceInactive(String serviceName) {
            super("SERVICE_INACTIVE", String.format("Service %s is not available", serviceName), HttpStatus.BAD_REQUEST);
        }
    }

    public static class InvalidPrice extends MaintenanceCatalogException {
        public InvalidPrice() {
            super("INVALID_PRICE", "Service price must be greater than zero", HttpStatus.BAD_REQUEST);
        }
    }

    public static class InvalidEstTime extends MaintenanceCatalogException {
        public InvalidEstTime() {
            super("INVALID_EST_TIME", "Estimated time must be greater than zero", HttpStatus.BAD_REQUEST);
        }
    }

    public static class DuplicateServiceName extends MaintenanceCatalogException {
        public DuplicateServiceName(String name) {
            super("DUPLICATE_SERVICE_NAME", String.format("Service with name %s already exists", name), HttpStatus.CONFLICT);
        }
    }
}

package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception cho Maintenance Catalog domain
 * Bao gồm: MaintenanceCatalog, MaintenanceCatalogModel, MaintenanceCatalogModelPart
 */
public class MaintenanceCatalogException extends BaseServiceException {

    protected MaintenanceCatalogException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // ==================== CATALOG EXCEPTIONS ====================

    /**
     * Catalog không hoạt động
     */
    public static class CatalogInactive extends MaintenanceCatalogException {
        public CatalogInactive(String catalogName) {
            super("CATALOG_INACTIVE",
                    String.format("Maintenance catalog '%s' is not available", catalogName),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Loại catalog không hợp lệ
     */
    public static class InvalidCatalogType extends MaintenanceCatalogException {
        public InvalidCatalogType(String type) {
            super("INVALID_CATALOG_TYPE",
                    String.format("Invalid maintenance catalog type: %s", type),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // ==================== CATALOG MODEL EXCEPTIONS ====================

    /**
     * Xe không support catalog này
     */
    public static class ModelNotSupported extends MaintenanceCatalogException {
        public ModelNotSupported(Long catalogId, Long modelId) {
            super("MODEL_NOT_SUPPORTED",
                    String.format("Model %d is not supported for catalog %d", modelId, catalogId),
                    HttpStatus.BAD_REQUEST);
        }

        public ModelNotSupported(String catalogName, String modelName) {
            super("MODEL_NOT_SUPPORTED",
                    String.format("Model '%s' is not supported for catalog '%s'", modelName, catalogName),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // ==================== CATALOG MODEL PART EXCEPTIONS ====================

    /**
     * Part không available
     */
    public static class PartNotAvailable extends MaintenanceCatalogException {
        public PartNotAvailable(String partName) {
            super("PART_NOT_AVAILABLE",
                    String.format("Part '%s' is not available", partName),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // ==================== GENERAL DOMAIN EXCEPTIONS ====================
    /**
     * Không có dịch vụ nào available cho xe này
     */
    public static class NoServicesAvailable extends MaintenanceCatalogException {
        public NoServicesAvailable(Long modelId) {
            super("NO_SERVICES_AVAILABLE",
                    String.format("No maintenance services available for model %d", modelId),
                    HttpStatus.NOT_FOUND);
        }

        public NoServicesAvailable(String modelName) {
            super("NO_SERVICES_AVAILABLE",
                    String.format("No maintenance services available for model '%s'", modelName),
                    HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Batch operation failed
     */
    public static class BatchOperationFailed extends MaintenanceCatalogException {
        public BatchOperationFailed(String details) {
            super("BATCH_OPERATION_FAILED",
                    String.format("Batch operation failed: %s", details),
                    HttpStatus.BAD_REQUEST);
        }
    }
}

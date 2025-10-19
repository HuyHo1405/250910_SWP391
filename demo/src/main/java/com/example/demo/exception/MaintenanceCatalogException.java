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
     * Catalog không active/available
     */
    public static class CatalogNotFound extends MaintenanceCatalogException {
        public CatalogNotFound(Long catalogId) {
            super("CATALOG_NOT_FOUND",
                    String.format("Maintenance catalog not found with id: %d", catalogId),
                    HttpStatus.NOT_FOUND);
        }
    }

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
     * Tên catalog bị trùng
     */
    public static class DuplicateCatalogName extends MaintenanceCatalogException {
        public DuplicateCatalogName(String name) {
            super("DUPLICATE_CATALOG_NAME",
                    String.format("Maintenance catalog with name '%s' already exists", name),
                    HttpStatus.CONFLICT);
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

    /**
     * Giá dịch vụ không hợp lệ
     */
    public static class InvalidPrice extends MaintenanceCatalogException {
        public InvalidPrice() {
            super("INVALID_PRICE",
                    "Service price must be greater than zero",
                    HttpStatus.BAD_REQUEST);
        }

        public InvalidPrice(String message) {
            super("INVALID_PRICE", message, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Thời gian ước tính không hợp lệ
     */
    public static class InvalidEstTime extends MaintenanceCatalogException {
        public InvalidEstTime() {
            super("INVALID_EST_TIME",
                    "Estimated time must be greater than zero",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // ==================== CATALOG MODEL EXCEPTIONS ====================

    /**
     * Catalog Model không tìm thấy
     */
    public static class CatalogModelNotFound extends MaintenanceCatalogException {
        public CatalogModelNotFound(Long catalogModelId) {
            super("CATALOG_MODEL_NOT_FOUND",
                    String.format("Catalog model not found with id: %d", catalogModelId),
                    HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Catalog Model đã tồn tại (duplicate)
     */
    public static class DuplicateCatalogModel extends MaintenanceCatalogException {
        public DuplicateCatalogModel(Long catalogId, Long modelId) {
            super("DUPLICATE_CATALOG_MODEL",
                    String.format("Catalog model already exists for catalog %d and model %d", catalogId, modelId),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * Vehicle model không tìm thấy
     */
    public static class VehicleModelNotFound extends MaintenanceCatalogException {
        public VehicleModelNotFound(Long modelId) {
            super("VEHICLE_MODEL_NOT_FOUND",
                    String.format("Vehicle model not found with id: %d", modelId),
                    HttpStatus.NOT_FOUND);
        }
    }

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
     * Catalog Model Part không tìm thấy
     */
    public static class CatalogModelPartNotFound extends MaintenanceCatalogException {
        public CatalogModelPartNotFound(Long partId) {
            super("CATALOG_MODEL_PART_NOT_FOUND",
                    String.format("Catalog model part not found with id: %d", partId),
                    HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Part đã tồn tại cho combo catalog + model
     */
    public static class DuplicatePart extends MaintenanceCatalogException {
        public DuplicatePart(Long catalogId, Long modelId, Long partId) {
            super("DUPLICATE_PART",
                    String.format("Part %d already exists for catalog %d and model %d", partId, catalogId, modelId),
                    HttpStatus.CONFLICT);
        }
    }

    /**
     * Part không tìm thấy trong hệ thống
     */
    public static class PartNotFound extends MaintenanceCatalogException {
        public PartNotFound(Long partId) {
            super("PART_NOT_FOUND",
                    String.format("Part not found with id: %d", partId),
                    HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Số lượng part không hợp lệ
     */
    public static class InvalidQuantity extends MaintenanceCatalogException {
        public InvalidQuantity() {
            super("INVALID_QUANTITY",
                    "Part quantity must be greater than zero",
                    HttpStatus.BAD_REQUEST);
        }

        public InvalidQuantity(String message) {
            super("INVALID_QUANTITY", message, HttpStatus.BAD_REQUEST);
        }
    }

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
     * Vehicle không tìm thấy theo VIN
     */
    public static class VehicleNotFoundByVin extends MaintenanceCatalogException {
        public VehicleNotFoundByVin(String vin) {
            super("VEHICLE_NOT_FOUND",
                    String.format("Vehicle not found with VIN: %s", vin),
                    HttpStatus.NOT_FOUND);
        }
    }

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

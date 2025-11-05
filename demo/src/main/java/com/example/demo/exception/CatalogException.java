package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception cho Maintenance Catalog domain
 * Bao gồm: MaintenanceCatalog, MaintenanceCatalogModel, MaintenanceCatalogModelPart
 */

public class CatalogException extends BaseServiceException {

    protected CatalogException(String code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    // ==================== CATALOG EXCEPTIONS ====================

    /**
     * Catalog không hoạt động
     */

    public static class CatalogInactive extends CatalogException {
        public CatalogInactive(String catalogName) {
            super("CATALOG_INACTIVE",
                    String.format("Danh mục bảo trì '%s' không có sẵn", catalogName),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Loại catalog không hợp lệ
     */

    public static class InvalidCatalogType extends CatalogException {
        public InvalidCatalogType(String type) {
            super("INVALID_CATALOG_TYPE",
                    String.format("Loại danh mục bảo trì không hợp lệ: %s", type),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // ==================== CATALOG MODEL EXCEPTIONS ====================

    /**
     * Xe không support catalog này
     */

    public static class ModelNotSupported extends CatalogException {
        public ModelNotSupported(Long catalogId, Long modelId) {
            super("MODEL_NOT_SUPPORTED",
                    String.format("Mẫu xe %d không được hỗ trợ cho danh mục %d", modelId, catalogId),
                    HttpStatus.BAD_REQUEST);
        }

        public ModelNotSupported(String catalogName, String modelName) {
            super("MODEL_NOT_SUPPORTED",
                    String.format("Mẫu xe '%s' không được hỗ trợ cho danh mục '%s'", modelName, catalogName),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // ==================== CATALOG MODEL PART EXCEPTIONS ====================

    /**
     * Part không available
     */

    public static class PartNotAvailable extends CatalogException {
        public PartNotAvailable(String partName) {
            super("PART_NOT_AVAILABLE",
                    String.format("Phụ tùng '%s' không có sẵn", partName),
                    HttpStatus.BAD_REQUEST);
        }
    }

    // ==================== GENERAL DOMAIN EXCEPTIONS ====================

    /**
     * Không có dịch vụ nào available cho xe này
     */

    public static class NoServicesAvailable extends CatalogException {
        public NoServicesAvailable(Long modelId) {
            super("NO_SERVICES_AVAILABLE",
                    String.format("Không có dịch vụ bảo trì nào có sẵn cho mẫu xe %d", modelId),
                    HttpStatus.NOT_FOUND);
        }

        public NoServicesAvailable(String modelName) {
            super("NO_SERVICES_AVAILABLE",
                    String.format("Không có dịch vụ bảo trì nào có sẵn cho mẫu xe '%s'", modelName),
                    HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Batch operation failed
     */

    public static class BatchOperationFailed extends CatalogException {
        public BatchOperationFailed(String details) {
            super("BATCH_OPERATION_FAILED",
                    String.format("Thao tác hàng loạt thất bại: %s", details),
                    HttpStatus.BAD_REQUEST);
        }
    }
}

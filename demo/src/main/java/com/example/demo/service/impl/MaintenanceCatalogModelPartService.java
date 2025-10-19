package com.example.demo.service.impl;

import com.example.demo.exception.MaintenanceCatalogException;
import com.example.demo.model.dto.MaintenanceCatalogModelPartRequest;
import com.example.demo.model.dto.MaintenanceCatalogModelPartResponse;
import com.example.demo.model.entity.MaintenanceCatalog;
import com.example.demo.model.entity.MaintenanceCatalogModelPart;
import com.example.demo.model.entity.Part;
import com.example.demo.model.entity.VehicleModel;
import com.example.demo.repo.MaintenanceCatalogModelPartRepo;
import com.example.demo.repo.MaintenanceCatalogRepo;
import com.example.demo.repo.PartRepo;
import com.example.demo.repo.VehicleModelRepo;
import com.example.demo.service.interfaces.IMaintenanceCatalogModelPartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MaintenanceCatalogModelPartService implements IMaintenanceCatalogModelPartService {

    private final MaintenanceCatalogModelPartRepo catalogModelPartRepo;
    private final MaintenanceCatalogRepo catalogRepo;
    private final VehicleModelRepo vehicleModelRepo;
    private final PartRepo partRepo;
    private final AccessControlService accessControlService;

    @Override
    public MaintenanceCatalogModelPartResponse create(MaintenanceCatalogModelPartRequest request) {
        log.info("Creating catalog model part: catalogId={}, modelId={}, partId={}",
                request.getMaintenanceCatalogId(), request.getModelId(), request.getPartId());

        // 1. Check permission: MAINTENANCE_SERVICE + create
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "create");

        // 2. Validate catalog exists
        MaintenanceCatalog catalog = catalogRepo.findById(request.getMaintenanceCatalogId())
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogNotFound(request.getMaintenanceCatalogId()));

        // 3. Validate vehicle model exists
        VehicleModel vehicleModel = vehicleModelRepo.findById(request.getModelId())
                .orElseThrow(() -> new MaintenanceCatalogException.VehicleModelNotFound(request.getModelId()));

        // 4. Validate part exists
        Part part = partRepo.findById(request.getPartId())
                .orElseThrow(() -> new MaintenanceCatalogException.PartNotFound(request.getPartId()));

        // 5. Check if combination already exists
        boolean exists = catalogModelPartRepo.existsByMaintenanceCatalogIdAndVehicleModelIdAndPartId(
                request.getMaintenanceCatalogId(),
                request.getModelId(),
                request.getPartId());

        if (exists) {
            throw new MaintenanceCatalogException.DuplicatePart(
                    request.getMaintenanceCatalogId(),
                    request.getModelId(),
                    request.getPartId());
        }

        // 6. Validate quantity
        if (request.getQuantityRequired() != null && request.getQuantityRequired() <= 0) {
            throw new MaintenanceCatalogException.InvalidQuantity();
        }

        // 7. Create new catalog model part
        MaintenanceCatalogModelPart catalogModelPart = MaintenanceCatalogModelPart.builder()
                .maintenanceCatalog(catalog)
                .vehicleModel(vehicleModel)
                .part(part)
                .quantityRequired(request.getQuantityRequired() != null ? request.getQuantityRequired() : 1)
                .isOptional(request.getIsOptional() != null ? request.getIsOptional() : false)
                .notes(request.getNotes())
                .build();

        MaintenanceCatalogModelPart saved = catalogModelPartRepo.save(catalogModelPart);
        log.info("Created catalog model part successfully: id={}", saved.getId());

        return toResponse(saved);
    }

    @Override
    public List<MaintenanceCatalogModelPartResponse> createBatch(List<MaintenanceCatalogModelPartRequest> requests) {
        log.info("Creating batch catalog model parts: count={}", requests.size());

        // Check permission once for batch operation
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "create");

        try {
            return requests.stream()
                    .map(this::createWithoutPermissionCheck) // Use internal method to avoid redundant checks
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Batch creation failed", e);
            throw new MaintenanceCatalogException.BatchOperationFailed(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceCatalogModelPartResponse> findByCatalogAndModel(Long catalogId, Long modelId, Long partId) {
        log.info("Finding catalog model parts: catalogId={}, modelId={}, partId={}", catalogId, modelId, partId);

        // Check permission: MAINTENANCE_SERVICE + read
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");

        // Validate catalog exists
        catalogRepo.findById(catalogId)
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogNotFound(catalogId));

        // Validate vehicle model exists
        vehicleModelRepo.findById(modelId)
                .orElseThrow(() -> new MaintenanceCatalogException.VehicleModelNotFound(modelId));

        List<MaintenanceCatalogModelPart> parts;

        if (partId != null) {
            // Find specific part
            MaintenanceCatalogModelPart part = catalogModelPartRepo
                    .findByMaintenanceCatalogIdAndVehicleModelIdAndPartId(catalogId, modelId, partId)
                    .orElseThrow(() -> new MaintenanceCatalogException.CatalogModelPartNotFound(partId));
            parts = List.of(part);
        } else {
            // Find all parts for this catalog-model combination
            parts = catalogModelPartRepo.findByMaintenanceCatalogIdAndVehicleModelId(catalogId, modelId);
        }

        log.info("Found {} catalog model parts", parts.size());
        return parts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceCatalogModelPartResponse update(Long catalogId, Long modelId, Long partId,
                                                      MaintenanceCatalogModelPartRequest request) {
        log.info("Updating catalog model part: catalogId={}, modelId={}, partId={}", catalogId, modelId, partId);

        // Check permission: MAINTENANCE_SERVICE + update
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "update");

        // Find existing catalog model part
        MaintenanceCatalogModelPart catalogModelPart = catalogModelPartRepo
                .findByMaintenanceCatalogIdAndVehicleModelIdAndPartId(catalogId, modelId, partId)
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogModelPartNotFound(partId));

        // Validate quantity if provided
        if (request.getQuantityRequired() != null) {
            if (request.getQuantityRequired() <= 0) {
                throw new MaintenanceCatalogException.InvalidQuantity();
            }
            catalogModelPart.setQuantityRequired(request.getQuantityRequired());
        }

        // Update other fields
        if (request.getIsOptional() != null) {
            catalogModelPart.setIsOptional(request.getIsOptional());
        }
        if (request.getNotes() != null) {
            catalogModelPart.setNotes(request.getNotes());
        }

        MaintenanceCatalogModelPart updated = catalogModelPartRepo.save(catalogModelPart);
        log.info("Updated catalog model part successfully: id={}", updated.getId());

        return toResponse(updated);
    }

    @Override
    public void delete(Long catalogId, Long modelId, Long partId) {
        log.info("Deleting catalog model part: catalogId={}, modelId={}, partId={}", catalogId, modelId, partId);

        // Check permission: MAINTENANCE_SERVICE + delete
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "delete");

        // Verify the part exists before deleting
        MaintenanceCatalogModelPart catalogModelPart = catalogModelPartRepo
                .findByMaintenanceCatalogIdAndVehicleModelIdAndPartId(catalogId, modelId, partId)
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogModelPartNotFound(partId));

        catalogModelPartRepo.delete(catalogModelPart);
        log.info("Deleted catalog model part successfully");
    }

    @Override
    public void deleteBatch(Long catalogId, Long modelId) {
        log.info("Deleting batch catalog model parts: catalogId={}, modelId={}", catalogId, modelId);

        // Check permission: MAINTENANCE_SERVICE + delete
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "delete");

        // Validate catalog exists
        catalogRepo.findById(catalogId)
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogNotFound(catalogId));

        // Validate model exists
        vehicleModelRepo.findById(modelId)
                .orElseThrow(() -> new MaintenanceCatalogException.VehicleModelNotFound(modelId));

        // Delete all parts for this catalog-model combination
        List<MaintenanceCatalogModelPart> parts = catalogModelPartRepo
                .findByMaintenanceCatalogIdAndVehicleModelId(catalogId, modelId);

        if (!parts.isEmpty()) {
            catalogModelPartRepo.deleteAll(parts);
            log.info("Deleted {} catalog model parts successfully", parts.size());
        } else {
            log.info("No parts found to delete");
        }
    }

    // Internal method for batch operations (skips permission check)
    private MaintenanceCatalogModelPartResponse createWithoutPermissionCheck(MaintenanceCatalogModelPartRequest request) {
        MaintenanceCatalog catalog = catalogRepo.findById(request.getMaintenanceCatalogId())
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogNotFound(request.getMaintenanceCatalogId()));

        VehicleModel vehicleModel = vehicleModelRepo.findById(request.getModelId())
                .orElseThrow(() -> new MaintenanceCatalogException.VehicleModelNotFound(request.getModelId()));

        Part part = partRepo.findById(request.getPartId())
                .orElseThrow(() -> new MaintenanceCatalogException.PartNotFound(request.getPartId()));

        boolean exists = catalogModelPartRepo.existsByMaintenanceCatalogIdAndVehicleModelIdAndPartId(
                request.getMaintenanceCatalogId(), request.getModelId(), request.getPartId());

        if (exists) {
            throw new MaintenanceCatalogException.DuplicatePart(
                    request.getMaintenanceCatalogId(), request.getModelId(), request.getPartId());
        }

        if (request.getQuantityRequired() != null && request.getQuantityRequired() <= 0) {
            throw new MaintenanceCatalogException.InvalidQuantity();
        }

        MaintenanceCatalogModelPart catalogModelPart = MaintenanceCatalogModelPart.builder()
                .maintenanceCatalog(catalog)
                .vehicleModel(vehicleModel)
                .part(part)
                .quantityRequired(request.getQuantityRequired() != null ? request.getQuantityRequired() : 1)
                .isOptional(request.getIsOptional() != null ? request.getIsOptional() : false)
                .notes(request.getNotes())
                .build();

        MaintenanceCatalogModelPart saved = catalogModelPartRepo.save(catalogModelPart);
        return toResponse(saved);
    }

    // Helper method to convert entity to response DTO
    private MaintenanceCatalogModelPartResponse toResponse(MaintenanceCatalogModelPart catalogModelPart) {
        return MaintenanceCatalogModelPartResponse.builder()
                .partId(catalogModelPart.getPart().getId())
                .partName(catalogModelPart.getPart().getName())
                .quantityRequired(catalogModelPart.getQuantityRequired())
                .isOptional(catalogModelPart.getIsOptional())
                .notes(catalogModelPart.getNotes())
                .build();
    }
}

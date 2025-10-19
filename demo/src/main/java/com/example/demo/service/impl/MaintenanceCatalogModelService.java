package com.example.demo.service.impl;

import com.example.demo.exception.MaintenanceCatalogException;
import com.example.demo.model.dto.MaintenanceCatalogModelRequest;
import com.example.demo.model.dto.MaintenanceCatalogModelResponse;
import com.example.demo.model.dto.MaintenanceCatalogModelPartResponse;
import com.example.demo.model.entity.MaintenanceCatalog;
import com.example.demo.model.entity.MaintenanceCatalogModel;
import com.example.demo.model.entity.MaintenanceCatalogModelPart;
import com.example.demo.model.entity.VehicleModel;
import com.example.demo.repo.MaintenanceCatalogModelRepo;
import com.example.demo.repo.MaintenanceCatalogRepo;
import com.example.demo.repo.VehicleModelRepo;
import com.example.demo.repo.MaintenanceCatalogModelPartRepo;
import com.example.demo.service.interfaces.IMaintenanceCatalogModelService;
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
public class MaintenanceCatalogModelService implements IMaintenanceCatalogModelService {

    private final MaintenanceCatalogModelRepo catalogModelRepository;
    private final MaintenanceCatalogRepo catalogRepository;
    private final VehicleModelRepo vehicleModelRepository;
    private final MaintenanceCatalogModelPartRepo catalogModelPartRepository;
    private final AccessControlService accessControlService;

    @Override
    public MaintenanceCatalogModelResponse create(MaintenanceCatalogModelRequest request) {
        log.info("Create catalog model: catalogId={}, modelId={}",
                request.getMaintenanceCatalogId(), request.getModelId());
        // Phân quyền: MAINTENANCE_SERVICE + create
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "create");

        // Validate catalog
        MaintenanceCatalog catalog = catalogRepository.findById(request.getMaintenanceCatalogId())
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogNotFound(request.getMaintenanceCatalogId()));

        // Validate vehicle model
        VehicleModel vehicleModel = vehicleModelRepository.findById(request.getModelId())
                .orElseThrow(() -> new MaintenanceCatalogException.VehicleModelNotFound(request.getModelId()));

        // Không cho trùng catalog-model
        boolean exists = catalogModelRepository.existsByMaintenanceCatalogIdAndVehicleModelId(
                request.getMaintenanceCatalogId(), request.getModelId());
        if (exists) {
            throw new MaintenanceCatalogException.DuplicateCatalogModel(request.getMaintenanceCatalogId(), request.getModelId());
        }

        // Validate business: estTimeMinutes > 0, maintenancePrice >= 0
        if (request.getEstTimeMinutes() != null && request.getEstTimeMinutes() <= 0) {
            throw new MaintenanceCatalogException.InvalidEstTime();
        }
        if (request.getMaintenancePrice() != null && request.getMaintenancePrice() < 0) {
            throw new MaintenanceCatalogException.InvalidPrice();
        }

        // Create
        MaintenanceCatalogModel catalogModel = MaintenanceCatalogModel.builder()
                .maintenanceCatalog(catalog)
                .vehicleModel(vehicleModel)
                .estTimeMinutes(request.getEstTimeMinutes())
                .maintenancePrice(request.getMaintenancePrice())
                .notes(request.getNotes())
                .build();

        MaintenanceCatalogModel saved = catalogModelRepository.save(catalogModel);

        // Nếu có parts thì thêm batch, access vẫn đang ở quyền create (phải check trong service batch nếu có logic)
        // Xử lý thêm nếu muốn

        return toResponse(saved, false);
    }

    @Override
    public List<MaintenanceCatalogModelResponse> createBatch(List<MaintenanceCatalogModelRequest> requests) {
        log.info("Create batch catalog models: count={}", requests.size());
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "create");

        try {
            return requests.stream()
                    .map(this::createNoPermissionCheck)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Batch creation failed", e);
            throw new MaintenanceCatalogException.BatchOperationFailed(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceCatalogModelResponse> findByCatalogId(Long catalogId, Long modelId, boolean includeParts) {
        log.info("Find catalog models: catalogId={}, modelId={}, includeParts={}", catalogId, modelId, includeParts);
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");

        catalogRepository.findById(catalogId)
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogNotFound(catalogId));

        List<MaintenanceCatalogModel> catalogModels;
        if (modelId != null) {
            catalogModels = catalogModelRepository.findByMaintenanceCatalogIdAndVehicleModelId(catalogId, modelId);
        } else {
            catalogModels = catalogModelRepository.findByMaintenanceCatalogId(catalogId);
        }

        return catalogModels.stream()
                .map(model -> toResponse(model, includeParts))
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceCatalogModelResponse updateByCatalogAndModel(Long catalogId, Long modelId,
                                                                   MaintenanceCatalogModelRequest request) {
        log.info("Update catalog model: catalogId={}, modelId={}", catalogId, modelId);
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "update");

        MaintenanceCatalogModel catalogModel = catalogModelRepository
                .findFirstByMaintenanceCatalogIdAndVehicleModelId(catalogId, modelId)
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogModelNotFound(catalogId));

        if (request.getEstTimeMinutes() != null) {
            if (request.getEstTimeMinutes() <= 0) throw new MaintenanceCatalogException.InvalidEstTime();
            catalogModel.setEstTimeMinutes(request.getEstTimeMinutes());
        }
        if (request.getMaintenancePrice() != null) {
            if (request.getMaintenancePrice() < 0) throw new MaintenanceCatalogException.InvalidPrice();
            catalogModel.setMaintenancePrice(request.getMaintenancePrice());
        }
        if (request.getNotes() != null) catalogModel.setNotes(request.getNotes());

        MaintenanceCatalogModel updated = catalogModelRepository.save(catalogModel);
        return toResponse(updated, false);
    }

    @Override
    public void delete(Long catalogId, Long modelId) {
        log.info("Delete catalog model: catalogId={}, modelId={}", catalogId, modelId);
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "delete");

        MaintenanceCatalogModel catalogModel = catalogModelRepository
                .findFirstByMaintenanceCatalogIdAndVehicleModelId(catalogId, modelId)
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogModelNotFound(catalogId));
        catalogModelRepository.delete(catalogModel);
    }

    @Override
    public void deleteBatch(long catalogId) {
        log.info("Delete batch catalog models: catalogId={}", catalogId);
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "delete");

        catalogRepository.findById(catalogId)
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogNotFound(catalogId));
        List<MaintenanceCatalogModel> catalogModels = catalogModelRepository.findByMaintenanceCatalogId(catalogId);
        catalogModelRepository.deleteAll(catalogModels);
    }

    // Internal method for batch logic, permission already checked
    private MaintenanceCatalogModelResponse createNoPermissionCheck(MaintenanceCatalogModelRequest request) {
        MaintenanceCatalog catalog = catalogRepository.findById(request.getMaintenanceCatalogId())
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogNotFound(request.getMaintenanceCatalogId()));
        VehicleModel vehicleModel = vehicleModelRepository.findById(request.getModelId())
                .orElseThrow(() -> new MaintenanceCatalogException.VehicleModelNotFound(request.getModelId()));
        boolean exists = catalogModelRepository.existsByMaintenanceCatalogIdAndVehicleModelId(request.getMaintenanceCatalogId(), request.getModelId());
        if (exists) throw new MaintenanceCatalogException.DuplicateCatalogModel(request.getMaintenanceCatalogId(), request.getModelId());
        if (request.getEstTimeMinutes() != null && request.getEstTimeMinutes() <= 0) throw new MaintenanceCatalogException.InvalidEstTime();
        if (request.getMaintenancePrice() != null && request.getMaintenancePrice() < 0) throw new MaintenanceCatalogException.InvalidPrice();
        MaintenanceCatalogModel catalogModel = MaintenanceCatalogModel.builder()
                .maintenanceCatalog(catalog)
                .vehicleModel(vehicleModel)
                .estTimeMinutes(request.getEstTimeMinutes())
                .maintenancePrice(request.getMaintenancePrice())
                .notes(request.getNotes())
                .build();
        MaintenanceCatalogModel saved = catalogModelRepository.save(catalogModel);
        return toResponse(saved, false);
    }

    private MaintenanceCatalogModelResponse toResponse(MaintenanceCatalogModel model, boolean includeParts) {
        VehicleModel vehicleModel = model.getVehicleModel();
        MaintenanceCatalogModelResponse.MaintenanceCatalogModelResponseBuilder builder = MaintenanceCatalogModelResponse.builder()
                .modelId(vehicleModel.getId())
                .modelName(vehicleModel.getModelName())
                .modelBrand(vehicleModel.getBrandName())
                .modelYear(vehicleModel.getYearIntroduce())
                .estTimeMinutes(model.getEstTimeMinutes())
                .maintenancePrice(model.getMaintenancePrice())
                .notes(model.getNotes())
                .createdAt(model.getCreatedAt());

        if (includeParts) {
            List<MaintenanceCatalogModelPart> parts = catalogModelPartRepository
                    .findByMaintenanceCatalogIdAndVehicleModelId(model.getMaintenanceCatalog().getId(), vehicleModel.getId());
            List<MaintenanceCatalogModelPartResponse> partResponses = parts.stream()
                    .map(this::toPartResponse)
                    .collect(Collectors.toList());
            builder.parts(partResponses);
        }
        return builder.build();
    }

    private MaintenanceCatalogModelPartResponse toPartResponse(MaintenanceCatalogModelPart part) {
        return MaintenanceCatalogModelPartResponse.builder()
                .partId(part.getPart().getId())
                .partName(part.getPart().getName())
                .quantityRequired(part.getQuantityRequired())
                .isOptional(part.getIsOptional())
                .notes(part.getNotes())
                .build();
    }
}

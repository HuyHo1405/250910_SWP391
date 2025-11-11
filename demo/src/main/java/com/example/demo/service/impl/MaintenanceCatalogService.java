package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.exception.CatalogException;
import com.example.demo.model.dto.*;
import com.example.demo.model.entity.*;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.modelEnum.MaintenanceCatalogCategory;
import com.example.demo.repo.MaintenanceCatalogRepo;
import com.example.demo.repo.PartRepo;
import com.example.demo.repo.VehicleModelRepo;
import com.example.demo.repo.VehicleRepo;
import com.example.demo.service.interfaces.IMaintenanceCatalogModelPartService;
import com.example.demo.service.interfaces.IMaintenanceCatalogService;
import com.example.demo.service.interfaces.IMaintenanceCatalogModelService;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceCatalogService implements IMaintenanceCatalogService {

    private final IMaintenanceCatalogModelService catalogModelService;
    private final AccessControlService accessControlService;
    private final IMaintenanceCatalogModelPartService maintenanceCatalogModelPartService;

    private final MaintenanceCatalogRepo catalogRepository;
    private final VehicleRepo vehicleRepository;
    private final VehicleModelRepo vehicleModelRepository;
    private final PartRepo  partRepository;

    @Override
    @Transactional
    public CatalogResponse create(CatalogRequest request) {
        // ✅ Phân quyền: Chỉ ADMIN/STAFF có quyền create catalog
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "create");

        // ✅ Validation: Kiểm tra tên catalog bị trùng
        if (catalogRepository.existsByName(request.getName())) {
            throw new CommonException.AlreadyExists("Dịch vụ", "Tên",  request.getName());
        }

        MaintenanceCatalog catalog = MaintenanceCatalog.builder()
                .name(request.getName())
                .description(request.getDescription())
                .maintenanceServiceCategory(request.getMaintenanceServiceCategory())
                .status(EntityStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        processNestedModels(catalog, request.getModels());

        MaintenanceCatalog saved = catalogRepository.save(catalog);
        log.info("Created new maintenance catalog: id={}, name={}", saved.getId(), saved.getName());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CatalogResponse update(Long id, CatalogRequest request) {
        // ✅ Phân quyền: Chỉ ADMIN/STAFF có quyền update catalog
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "update");

        // ✅ Exception: Sử dụng MaintenanceCatalogException.CatalogNotFound
        MaintenanceCatalog catalog = catalogRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", id));

        // ✅ Validation: Kiểm tra tên trùng (trừ catalog hiện tại)
        if (!catalog.getName().equals(request.getName()) &&
                catalogRepository.existsByName(request.getName())) {
            throw new CommonException.AlreadyExists("Dịch vụ", "Tên",  request.getName());
        }

        catalog.setName(request.getName());
        catalog.setDescription(request.getDescription());
        catalog.setMaintenanceServiceCategory(request.getMaintenanceServiceCategory());

        MaintenanceCatalog updated = catalogRepository.save(catalog);
        log.info("Updated maintenance catalog: id={}, name={}", updated.getId(), updated.getName());

        catalogModelService.syncBatch(catalog.getId(), request.getModels());
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogResponse findById(Long id) {
        // ✅ Phân quyền: Tất cả user có quyền đọc catalog (theo data.sql)
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");

        // ✅ Exception: Sử dụng MaintenanceCatalogException.CatalogNotFound
        MaintenanceCatalog catalog = catalogRepository.findById(id)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", id));

        // ✅ Kiểm tra catalog có active không
        if (catalog.getStatus() != EntityStatus.ACTIVE) {
            throw new CatalogException.CatalogInactive(catalog.getName());
        }

        return mapToResponse(catalog);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatalogResponse> findAllPaged(
            @Nullable MaintenanceCatalogCategory type,
            @Nullable String vin,
            @Nullable Long modelId,
            Pageable pageable
    ) {
        // ✅ Phân quyền: Tất cả user có quyền đọc catalog
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");

        // ✅ Nếu truyền VIN, validate VIN có tồn tại không
        if (vin != null && !vin.isEmpty()) {
            if (!vehicleRepository.existsByVinAndEntityStatus(vin, EntityStatus.ACTIVE)) {
                throw new CommonException.NotFound("Xe với mã VIN", vin);
            }
        }

        // ✅ Nếu truyền modelId, validate modelId có tồn tại không
        if (modelId != null) {
            if (!vehicleModelRepository.existsById(modelId)) {
                throw new CommonException.NotFound("Mẫu xe với Id", modelId);
            }
        }

        Page<MaintenanceCatalog> catalogsPage = catalogRepository.findByTypeAndVinPaged(type, vin, pageable);

        // ✅ Nếu không có catalog nào, throw NoServicesAvailable
        if (catalogsPage.isEmpty() && vin != null) {
            throw new CatalogException.NoServicesAvailable("VIN: " + vin);
        }

        // Map Page<MaintenanceCatalog> to Page<CatalogResponse>
        Page<CatalogResponse> responsePage = catalogsPage.map(catalog -> {
            // Chỉ lấy catalog ACTIVE
            if (catalog.getStatus() != EntityStatus.ACTIVE) {
                return null;
            }

            CatalogResponse response = mapToResponse(catalog);

            // Filter by modelId if provided
            if (modelId != null) {
                List<CatalogModelResponse> filteredModels = response.getModels().stream()
                        .filter(model -> model.getModelId().equals(modelId))
                        .collect(Collectors.toList());

                if (filteredModels.isEmpty()) {
                    return null;
                }
                response.setModels(filteredModels);
            }

            return response;
        });

        // Filter out null responses (inactive catalogs or catalogs without matching models)
        // Note: Page doesn't support direct filtering, so we convert to PageImpl with filtered content
        List<CatalogResponse> filteredContent = responsePage.getContent().stream()
                .filter(response -> response != null)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(
                filteredContent,
                pageable,
                catalogsPage.getTotalElements()
        );
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // ✅ Phân quyền: Chỉ ADMIN/STAFF có quyền delete
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "delete");

        MaintenanceCatalog entity = catalogRepository.findById(id)
                .orElseThrow(() ->  new CommonException.NotFound("Dịch vụ với Id", id));

        try {
            // Delete all associated catalog-model relationships first
            log.info("Deleted parts mapping with maintenance catalog: id={}", id);
            maintenanceCatalogModelPartService.deleteBatch(id);
            log.info("Deleted vehicle models mapping with maintenance catalog: id={}", id);
            catalogModelService.syncBatch(id, new ArrayList<>());
            log.info("Disable maintenance catalog: id={}", id);
            entity.setStatus(EntityStatus.ARCHIVED);
            catalogRepository.save(entity);
        } catch (Exception e) {
            log.error("Failed to delete catalog id={}: {}", id, e.getMessage());
            throw new CatalogException.BatchOperationFailed(
                    "Không thể xóa dịch vụ: " + e.getMessage()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EnumSchemaResponse getCategoryEnumSchema() {
        // ✅ Phân quyền: Tất cả user có quyền đọc enum schema
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");

        // Lấy tất cả values tiếng Việt của MaintenanceCatalogCategory enum
        List<String> categoryNames = Arrays.stream(MaintenanceCatalogCategory.values())
                .map(MaintenanceCatalogCategory::getVietnameseName)
                .collect(Collectors.toList());

        return new EnumSchemaResponse(
                "MaintenanceCatalogCategory",
                categoryNames,
                "Danh sách loại dịch vụ bảo dưỡng"
        );
    }

    private void processNestedModels(MaintenanceCatalog catalog, List<CatalogModelRequest> modelRequests) {

        for (CatalogModelRequest modelDto : modelRequests) {
            // A. Tìm VehicleModel
            VehicleModel vehicleModel = vehicleModelRepository.findById(modelDto.getModelId())
                    .orElseThrow(() -> new CommonException.NotFound("Mẫu xe với Id", modelDto.getModelId()));

            // B. Tạo thằng con (CatalogModel)
            MaintenanceCatalogModel catalogModel = MaintenanceCatalogModel.builder()
                    .vehicleModel(vehicleModel)
                    .estTimeMinutes(modelDto.getEstTimeMinutes())
                    .maintenancePrice(modelDto.getMaintenancePrice())
                    .notes(modelDto.getNotes())
                    .status(EntityStatus.ACTIVE)
                    .build();

            // C. Xử lý thằng cháu (Parts)
            if (modelDto.getParts() != null && !modelDto.getParts().isEmpty()) {
                for (CatalogModelPartRequest partDto : modelDto.getParts()) {

                    Part part = partRepository.findById(partDto.getPartId())
                            .orElseThrow(() -> new CommonException.NotFound("Phụ tùng với Id", partDto.getPartId()));

                    MaintenanceCatalogModelPart catalogPart = MaintenanceCatalogModelPart.builder()
                            .part(part)
                            .quantityRequired(partDto.getQuantityRequired())
                            .isOptional(partDto.getIsOptional())
                            .notes(partDto.getNotes())
                            .build();

                    // Liên kết Cháu -> Con
                    catalogModel.addPart(catalogPart);
                }
            }
            // Liên kết Con -> Cha
            catalog.addModel(catalogModel);
        }
    }

    private CatalogResponse mapToResponse(MaintenanceCatalog catalog) {
        List<CatalogModelResponse> models = catalogModelService.getModels(catalog.getId());

        return CatalogResponse.builder()
                .id(catalog.getId())
                .name(catalog.getName())
                .description(catalog.getDescription())
                .maintenanceServiceCategory(catalog.getMaintenanceServiceCategory().getVietnameseName())
                .status(catalog.getStatus())
                .createdAt(catalog.getCreatedAt())
                .models(models)
                .build();
    }
}

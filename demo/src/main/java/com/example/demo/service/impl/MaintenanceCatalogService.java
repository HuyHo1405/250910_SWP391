package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.exception.MaintenanceCatalogException;
import com.example.demo.model.dto.MaintenanceCatalogRequest;
import com.example.demo.model.dto.MaintenanceCatalogResponse;
import com.example.demo.model.dto.MaintenanceCatalogModelResponse;
import com.example.demo.model.entity.MaintenanceCatalog;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.modelEnum.MaintenanceCatalogType;
import com.example.demo.repo.MaintenanceCatalogRepo;
import com.example.demo.repo.VehicleRepo;
import com.example.demo.service.interfaces.IMaintenanceCatalogService;
import com.example.demo.service.interfaces.IMaintenanceCatalogModelService;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceCatalogService implements IMaintenanceCatalogService {

    private final MaintenanceCatalogRepo catalogRepository;
    private final VehicleRepo vehicleRepository;
    private final IMaintenanceCatalogModelService catalogModelService;
    private final AccessControlService accessControlService;

    @Override
    @Transactional
    public MaintenanceCatalogResponse create(MaintenanceCatalogRequest request) {
        // ✅ Phân quyền: Chỉ ADMIN/STAFF có quyền create catalog
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "create");

        // ✅ Validation: Kiểm tra tên catalog bị trùng
        if (catalogRepository.existsByName(request.getName())) {
            throw new MaintenanceCatalogException.DuplicateCatalogName(request.getName());
        }

        // ✅ Validation: Kiểm tra giá hợp lệ
        if (request.getCurrentPrice() == null || request.getCurrentPrice() <= 0) {
            throw new MaintenanceCatalogException.InvalidPrice();
        }

        // ✅ Validation: Kiểm tra thời gian ước tính
        if (request.getEstTimeMinutes() == null || request.getEstTimeMinutes() <= 0) {
            throw new MaintenanceCatalogException.InvalidEstTime();
        }

        MaintenanceCatalog catalog = MaintenanceCatalog.builder()
                .name(request.getName())
                .description(request.getDescription())
                .maintenanceServiceType(request.getMaintenanceServiceType())
                .estTimeMinutes(request.getEstTimeMinutes())
                .currentPrice(request.getCurrentPrice())
                .status(EntityStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        MaintenanceCatalog saved = catalogRepository.save(catalog);
        log.info("Created new maintenance catalog: id={}, name={}", saved.getId(), saved.getName());

        return mapToResponse(saved, false);
    }

    @Override
    @Transactional
    public MaintenanceCatalogResponse update(Long id, MaintenanceCatalogRequest request) {
        // ✅ Phân quyền: Chỉ ADMIN/STAFF có quyền update catalog
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "update");

        // ✅ Exception: Sử dụng MaintenanceCatalogException.CatalogNotFound
        MaintenanceCatalog catalog = catalogRepository.findById(id)
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogNotFound(id));

        // ✅ Validation: Kiểm tra tên trùng (trừ catalog hiện tại)
        if (!catalog.getName().equals(request.getName()) &&
                catalogRepository.existsByName(request.getName())) {
            throw new MaintenanceCatalogException.DuplicateCatalogName(request.getName());
        }

        // ✅ Validation: Kiểm tra giá và thời gian
        if (request.getCurrentPrice() != null && request.getCurrentPrice() <= 0) {
            throw new MaintenanceCatalogException.InvalidPrice();
        }
        if (request.getEstTimeMinutes() != null && request.getEstTimeMinutes() <= 0) {
            throw new MaintenanceCatalogException.InvalidEstTime();
        }

        catalog.setName(request.getName());
        catalog.setDescription(request.getDescription());
        catalog.setMaintenanceServiceType(request.getMaintenanceServiceType());
        catalog.setEstTimeMinutes(request.getEstTimeMinutes());
        catalog.setCurrentPrice(request.getCurrentPrice());

        MaintenanceCatalog updated = catalogRepository.save(catalog);
        log.info("Updated maintenance catalog: id={}, name={}", updated.getId(), updated.getName());

        return mapToResponse(updated, false);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceCatalogResponse findById(Long id, boolean includeModels) {
        // ✅ Phân quyền: Tất cả user có quyền đọc catalog (theo data.sql)
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");

        // ✅ Exception: Sử dụng MaintenanceCatalogException.CatalogNotFound
        MaintenanceCatalog catalog = catalogRepository.findById(id)
                .orElseThrow(() -> new MaintenanceCatalogException.CatalogNotFound(id));

        // ✅ Kiểm tra catalog có active không
        if (catalog.getStatus() != EntityStatus.ACTIVE) {
            throw new MaintenanceCatalogException.CatalogInactive(catalog.getName());
        }

        return mapToResponse(catalog, includeModels);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceCatalogResponse> findAll(
            @Nullable MaintenanceCatalogType type,
            @Nullable String vin,
            boolean includeModels
    ) {
        // ✅ Phân quyền: Tất cả user có quyền đọc catalog
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");

        // ✅ Nếu truyền VIN, validate VIN có tồn tại không
        if (vin != null && !vin.isEmpty()) {
            if (!vehicleRepository.existsByVinAndEntityStatus(vin, EntityStatus.ACTIVE)) {
                throw new MaintenanceCatalogException.VehicleNotFoundByVin(vin);
            }
        }

        List<MaintenanceCatalog> catalogs = catalogRepository.findByTypeAndVin(type, vin);

        // ✅ Nếu không có catalog nào, throw NoServicesAvailable
        if (catalogs.isEmpty() && vin != null) {
            throw new MaintenanceCatalogException.NoServicesAvailable("VIN: " + vin);
        }

        return catalogs.stream()
                .filter(c -> c.getStatus() == EntityStatus.ACTIVE) // Chỉ lấy catalog ACTIVE
                .map(catalog -> mapToResponse(catalog, includeModels))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // ✅ Phân quyền: Chỉ ADMIN/STAFF có quyền delete
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "delete");

        // ✅ Exception: Sử dụng MaintenanceCatalogException.CatalogNotFound
        if (!catalogRepository.existsById(id)) {
            throw new MaintenanceCatalogException.CatalogNotFound(id);
        }

        try {
            // Delete all associated catalog-model relationships first
            catalogModelService.deleteBatch(id);
            catalogRepository.deleteById(id);
            log.info("Deleted maintenance catalog: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete catalog id={}: {}", id, e.getMessage());
            throw new MaintenanceCatalogException.BatchOperationFailed(
                    "Cannot delete catalog: " + e.getMessage()
            );
        }
    }

    private MaintenanceCatalogResponse mapToResponse(MaintenanceCatalog catalog, boolean includeModels) {
        List<MaintenanceCatalogModelResponse> models = null;
        if (includeModels) {
            models = catalogModelService.findByCatalogId(catalog.getId(), null, true);
        }

        return MaintenanceCatalogResponse.builder()
                .id(catalog.getId())
                .name(catalog.getName())
                .description(catalog.getDescription())
                .maintenanceServiceType(catalog.getMaintenanceServiceType())
                .estTimeMinutes(catalog.getEstTimeMinutes())
                .currentPrice(catalog.getCurrentPrice())
                .status(catalog.getStatus())
                .createdAt(catalog.getCreatedAt())
                .models(models)
                .build();
    }
}

package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.MaintenanceCatalogModelRequest;
import com.example.demo.model.dto.MaintenanceCatalogModelResponse;
import com.example.demo.model.entity.MaintenanceCatalog;
import com.example.demo.model.entity.MaintenanceCatalogModel;
import com.example.demo.model.entity.VehicleModel;
import com.example.demo.repo.MaintenanceCatalogModelRepo;
import com.example.demo.repo.MaintenanceCatalogRepo;
import com.example.demo.repo.VehicleModelRepo;
import com.example.demo.service.interfaces.IMaintenanceCatalogModelService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MaintenanceCatalogModelService implements IMaintenanceCatalogModelService {

    private final AccessControlService accessControlService;
    private final MaintenanceCatalogModelPartService maintenanceCatalogModelPartService;

    private final MaintenanceCatalogModelRepo maintenanceCatalogModelRepo;
    private final VehicleModelRepo vehicleModelRepository;
    private final MaintenanceCatalogRepo catalogRepository;

    @Override
    @Transactional
    public List<MaintenanceCatalogModelResponse> syncBatch(
            Long catalogId,
            List<MaintenanceCatalogModelRequest> requests) {

        accessControlService.verifyCanAccessAllResources("MAINTENANCE_SERVICE", "update");

        // Validation catalog
        MaintenanceCatalog catalog = catalogRepository.findById(catalogId)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", catalogId));

        // 1. Lấy dữ liệu cũ
        List<MaintenanceCatalogModel> oldList = maintenanceCatalogModelRepo.findByMaintenanceCatalogId(catalogId);

        // 2. Tính toán Delta (chia ra 3 danh sách: ADD, UPDATE, DELETE)
        SyncDelta delta = calculateDelta(catalog, oldList, requests);

        // 3. Thực thi batch operations
        executeBatchOperations(delta);

        // 4. Trả về kết quả
        return maintenanceCatalogModelRepo.findByMaintenanceCatalogId(catalogId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceCatalogModelResponse updateByIds(Long catalogId, Long modelId, MaintenanceCatalogModelRequest request) {

        accessControlService.verifyCanAccessAllResources("MAINTENANCE_SERVICE", "update");

        catalogRepository.findById(catalogId)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", catalogId));

        vehicleModelRepository.findById(modelId)
                .orElseThrow(() -> new CommonException.NotFound("Mẫu xe với Id", modelId));

        MaintenanceCatalogModel entity = maintenanceCatalogModelRepo.findByMaintenanceCatalogIdAndVehicleModelId(catalogId, modelId)
                .orElseThrow(() -> new CommonException.NotFound(
                        "Dịch vụ với Id" + catalogId +
                                "cho mẫu xe với Id" + modelId));

        updateFields(entity, request);
        return toResponse(maintenanceCatalogModelRepo.save(entity), false);
    }

    @Override
    public MaintenanceCatalogModelResponse findByIds(Long catalogId, Long modelId, boolean includeParts) {

        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");

        catalogRepository.findById(catalogId)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", catalogId));

        vehicleModelRepository.findById(modelId)
                .orElseThrow(() -> new CommonException.NotFound("Mẫu xe với Id", modelId));

        MaintenanceCatalogModel entity = maintenanceCatalogModelRepo.findByMaintenanceCatalogIdAndVehicleModelId(catalogId, modelId)
                .orElseThrow(() -> new CommonException.NotFound(
                        "Dịch vụ với Id: " + catalogId +
                                "cho mẫu xe với Id" + modelId));

        return toResponse(entity, includeParts);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceCatalogModelResponse> getModels(Long catalogId) {

        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");

        catalogRepository.findById(catalogId)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", catalogId));

        return maintenanceCatalogModelRepo.findByMaintenanceCatalogId(catalogId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * UNIT 1: Tính toán Delta
     * Input: list cũ + list mới
     * Output: SyncDelta chứa 3 danh sách (toAdd, toUpdate, toDelete)
     */
    private SyncDelta calculateDelta(
            MaintenanceCatalog catalog,
            List<MaintenanceCatalogModel> oldList,
            List<MaintenanceCatalogModelRequest> requests) {

        // Chuyển sang Map để tra cứu O(1)
        Map<Long, MaintenanceCatalogModel> oldMap = oldList.stream()
                .collect(Collectors.toMap(
                        item -> item.getVehicleModel().getId(),
                        item -> item
                ));

        Map<Long, MaintenanceCatalogModelRequest> newMap = requests.stream()
                .collect(Collectors.toMap(
                        MaintenanceCatalogModelRequest::getModelId,
                        item -> item
                ));

        List<MaintenanceCatalogModel> toAdd = new ArrayList<>();
        List<MaintenanceCatalogModel> toUpdate = new ArrayList<>();
        List<Long> toDeleteIds = new ArrayList<>(); // Xóa theo id (PK)

        // Vòng 1: Duyệt list CŨ → xử lý UPDATE và DELETE
        for (MaintenanceCatalogModel oldItem : oldList) {
            Long modelId = oldItem.getVehicleModel().getId();
            MaintenanceCatalogModelRequest newItem = newMap.get(modelId);

            if (newItem == null) {
                // Không có trong list mới → DELETE
                toDeleteIds.add(oldItem.getId()); // Xóa theo PK
            } else {
                // Có trong cả 2 → kiểm tra UPDATE
                if (hasChanges(oldItem, newItem)) {
                    updateFields(oldItem, newItem);
                    toUpdate.add(oldItem);
                }
                newMap.remove(modelId); // Đã xử lý xong
            }
        }

        // Vòng 2: Những gì còn lại trong newMap → ADD
        for (MaintenanceCatalogModelRequest dto : newMap.values()) {
            MaintenanceCatalogModel newEntity = createNewEntity(catalog, dto);
            toAdd.add(newEntity);
        }

        return new SyncDelta(toAdd, toUpdate, toDeleteIds);
    }

    /**
     * UNIT 2: Kiểm tra có thay đổi không
     * So sánh từng field để quyết định UPDATE
     */
    private boolean hasChanges(MaintenanceCatalogModel oldItem, MaintenanceCatalogModelRequest newItem) {
        return !Objects.equals(oldItem.getEstTimeMinutes(), newItem.getEstTimeMinutes())
                || !Objects.equals(oldItem.getMaintenancePrice(), newItem.getMaintenancePrice())
                || !Objects.equals(oldItem.getNotes(), newItem.getNotes());
    }

    /**
     * UNIT 2.1: Cập nhật fields
     */
    private void updateFields(MaintenanceCatalogModel entity, MaintenanceCatalogModelRequest dto) {
        entity.setEstTimeMinutes(dto.getEstTimeMinutes());
        entity.setMaintenancePrice(dto.getMaintenancePrice());
        entity.setNotes(dto.getNotes());
    }

    /**
     * UNIT 2.2: Tạo entity mới từ DTO
     */
    private MaintenanceCatalogModel createNewEntity(MaintenanceCatalog catalog, MaintenanceCatalogModelRequest dto) {
        VehicleModel vehicleModel = vehicleModelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new CommonException.NotFound("Mẫu xe với Id", dto.getModelId()));

        return MaintenanceCatalogModel.builder()
                .maintenanceCatalog(catalog)
                .vehicleModel(vehicleModel)
                .estTimeMinutes(dto.getEstTimeMinutes())
                .maintenancePrice(dto.getMaintenancePrice())
                .notes(dto.getNotes())
                .build();
    }

    /**
     * UNIT 3: Thực thi batch operations
     * Xử lý 3 danh sách: INSERT, UPDATE, DELETE
     */
    private void executeBatchOperations(SyncDelta delta) {
        if (!delta.toAdd.isEmpty()) {
            maintenanceCatalogModelRepo.saveAll(delta.toAdd);
        }

        if (!delta.toUpdate.isEmpty()) {
            maintenanceCatalogModelRepo.saveAll(delta.toUpdate);
        }

        if (!delta.toDeleteIds.isEmpty()) {
            maintenanceCatalogModelRepo.deleteAllByIdInBatch(delta.toDeleteIds);
        }
    }

    /**
     * Helper class chứa kết quả tính Delta
     */
    @Value
    private static class SyncDelta {
        List<MaintenanceCatalogModel> toAdd;
        List<MaintenanceCatalogModel> toUpdate;
        List<Long> toDeleteIds; // DELETE theo PK
    }

    /**
     * Chuyển Entity → Response DTO
     */
    private MaintenanceCatalogModelResponse toResponse(MaintenanceCatalogModel entity, boolean includeParts) {
        return MaintenanceCatalogModelResponse.builder()
                .modelId(entity.getVehicleModel().getId())
                .modelName(entity.getVehicleModel().getModelName())
                .modelBrand(entity.getVehicleModel().getBrandName())
                .estTimeMinutes(entity.getEstTimeMinutes())
                .maintenancePrice(entity.getMaintenancePrice())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .parts(
                        includeParts ?
                                maintenanceCatalogModelPartService.getParts(
                                        entity.getMaintenanceCatalog().getId(),
                                        entity.getVehicleModel().getId()):
                                null
                )
                .build();
    }

    private MaintenanceCatalogModelResponse toResponse(MaintenanceCatalogModel entity) {
        return toResponse(entity, false);
    }
}

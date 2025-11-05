package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.CatalogModelRequest;
import com.example.demo.model.dto.CatalogModelResponse;
import com.example.demo.model.entity.MaintenanceCatalog;
import com.example.demo.model.entity.MaintenanceCatalogModel;
import com.example.demo.model.entity.VehicleModel;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.repo.MaintenanceCatalogModelPartRepo;
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
    private final MaintenanceCatalogModelPartRepo  catalogModelPartRepo;

    @Override
    @Transactional
    public List<CatalogModelResponse> syncBatch(
            Long catalogId,
            List<CatalogModelRequest> requests) {

        MaintenanceCatalog catalog = catalogRepository.findById(catalogId)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", catalogId));

        // 1. Lấy dữ liệu cũ
        List<MaintenanceCatalogModel> oldList = maintenanceCatalogModelRepo.findByMaintenanceCatalogId(catalogId);

        // 2. Tính toán Delta (chia ra 3 danh sách: ADD, UPDATE, DELETE)
        SyncDelta delta = calculateDelta(catalog, oldList, requests);

        // 3. Thực thi batch operations
        executeBatchOperations(delta, requests);

        // 4. Trả về kết quả
        return maintenanceCatalogModelRepo.findByMaintenanceCatalogId(catalogId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CatalogModelResponse updateByIds(Long catalogId, Long modelId, CatalogModelRequest request) {

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
    public CatalogModelResponse findByIds(Long catalogId, Long modelId, boolean includeParts) {

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
    public List<CatalogModelResponse> getModels(Long catalogId) {

        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");

        catalogRepository.findById(catalogId)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", catalogId));

        return maintenanceCatalogModelRepo.findByMaintenanceCatalogId(catalogId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private SyncDelta calculateDelta(
            MaintenanceCatalog catalog,
            List<MaintenanceCatalogModel> oldList,
            List<CatalogModelRequest> requests) {

        // Chuyển sang Map để tra cứu O(1)
        Map<Long, MaintenanceCatalogModel> oldMap = oldList.stream()
                .collect(Collectors.toMap(
                        item -> item.getVehicleModel().getId(),
                        item -> item
                ));

        Map<Long, CatalogModelRequest> newMap = requests.stream()
                .collect(Collectors.toMap(
                        CatalogModelRequest::getModelId,
                        item -> item
                ));

        List<MaintenanceCatalogModel> toAdd = new ArrayList<>();
        List<MaintenanceCatalogModel> toUpdate = new ArrayList<>();
        List<Long> toDeleteIds = new ArrayList<>(); // Xóa theo id (PK)

        // Vòng 1: Duyệt list CŨ → xử lý UPDATE và DELETE
        for (MaintenanceCatalogModel oldItem : oldList) {
            Long modelId = oldItem.getVehicleModel().getId();
            CatalogModelRequest newItem = newMap.get(modelId);

            if (newItem == null) {
                // Không có trong list mới → DELETE
                toDeleteIds.add(oldItem.getId()); // Xóa theo PK
            } else {
                // Có trong cả 2 → kiểm tra UPDATE
                boolean fieldsChanged = hasChanges(oldItem, newItem);
                boolean partsNeedSync = newItem.getParts() != null;

                if (fieldsChanged || partsNeedSync) { // 👈 Thêm điều kiện `partsNeedSync`
                    if (fieldsChanged) {
                        updateFields(oldItem, newItem); // Chỉ update field nếu thật sự thay đổi
                    }
                    toUpdate.add(oldItem); // Thêm vào list update để trigger part-sync
                }
                newMap.remove(modelId); // Đã xử lý xong
            }
        }

        // Vòng 2: Những gì còn lại trong newMap → ADD
        for (CatalogModelRequest dto : newMap.values()) {
            MaintenanceCatalogModel newEntity = createNewEntity(catalog, dto);
            toAdd.add(newEntity);
        }

        return new SyncDelta(toAdd, toUpdate, toDeleteIds);
    }

    private boolean hasChanges(MaintenanceCatalogModel oldItem, CatalogModelRequest newItem) {
        return !Objects.equals(oldItem.getEstTimeMinutes(), newItem.getEstTimeMinutes())
                || !Objects.equals(oldItem.getMaintenancePrice(), newItem.getMaintenancePrice())
                || !Objects.equals(oldItem.getNotes(), newItem.getNotes());
    }

    private void updateFields(MaintenanceCatalogModel entity, CatalogModelRequest dto) {
        entity.setEstTimeMinutes(dto.getEstTimeMinutes());
        entity.setMaintenancePrice(dto.getMaintenancePrice());
        entity.setNotes(dto.getNotes());
    }

    private MaintenanceCatalogModel createNewEntity(MaintenanceCatalog catalog, CatalogModelRequest dto) {
        VehicleModel vehicleModel = vehicleModelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new CommonException.NotFound("Mẫu xe với Id", dto.getModelId()));

        return MaintenanceCatalogModel.builder()
                .maintenanceCatalog(catalog)
                .vehicleModel(vehicleModel)
                .estTimeMinutes(dto.getEstTimeMinutes())
                .maintenancePrice(dto.getMaintenancePrice())
                .notes(dto.getNotes())
                .status(EntityStatus.ACTIVE)
                .build();
    }

    private void executeBatchOperations(SyncDelta delta, List<CatalogModelRequest> originalRequests) {
        // Tạo map để tra cứu request DTO gốc O(1)
        Map<Long, CatalogModelRequest> requestMap = originalRequests.stream()
                .collect(Collectors.toMap(
                        CatalogModelRequest::getModelId,
                        r -> r,
                        (r1, r2) -> r1 // Xử lý nếu có modelId trùng (lấy cái đầu)
                ));

        // === 1. XỬ LÝ DELETE ===
        // Phải xóa PART (con) trước khi xóa MODEL (cha)
        if (!delta.toDeleteIds.isEmpty()) {
            // `toDeleteIds` là List<Long> các PK của MaintenanceCatalogModel
            // Bạn cần thêm method này vào MaintenanceCatalogModelPartRepo
            catalogModelPartRepo.deleteAllByMaintenanceCatalogModelIdIn(delta.toDeleteIds);

            // Xóa MODEL (cha) sau
            maintenanceCatalogModelRepo.deleteAllByIdInBatch(delta.toDeleteIds);
        }

        // === 2. XỬ LÝ ADD ===
        if (!delta.toAdd.isEmpty()) {
            // Lưu MODEL (cha) trước để lấy ID
            List<MaintenanceCatalogModel> addedEntities = maintenanceCatalogModelRepo.saveAll(delta.toAdd);

            // Giờ lặp qua các entity đã lưu để sync PART (con)
            for (MaintenanceCatalogModel entity : addedEntities) {
                CatalogModelRequest req = requestMap.get(entity.getVehicleModel().getId());

                // Kiểm tra xem request gốc có 'parts' không
                if (req != null && req.getParts() != null) {
                    maintenanceCatalogModelPartService.syncBatch(entity.getId(), req.getParts());
                }
            }
        }

        // === 3. XỬ LÝ UPDATE ===
        if (!delta.toUpdate.isEmpty()) {
            // Lưu MODEL (cha)
            List<MaintenanceCatalogModel> updatedEntities = maintenanceCatalogModelRepo.saveAll(delta.toUpdate);

            // Giờ lặp qua các entity đã lưu để sync PART (con)
            for (MaintenanceCatalogModel entity : updatedEntities) {
                CatalogModelRequest req = requestMap.get(entity.getVehicleModel().getId());

                // Chỉ sync part nếu client *thực sự* gửi 'parts' trong request
                // Nếu req.getParts() == null, nghĩa là client không muốn đụng đến parts
                if (req != null && req.getParts() != null) {
                    maintenanceCatalogModelPartService.syncBatch(entity.getId(), req.getParts());
                }
            }
        }
    }

    @Value
    private static class SyncDelta {
        List<MaintenanceCatalogModel> toAdd;
        List<MaintenanceCatalogModel> toUpdate;
        List<Long> toDeleteIds; // DELETE theo PK
    }

    private CatalogModelResponse toResponse(MaintenanceCatalogModel entity, boolean includeParts) {
        return CatalogModelResponse.builder()
                .modelId(entity.getVehicleModel().getId())
                .modelName(entity.getVehicleModel().getModelName())
                .modelBrand(entity.getVehicleModel().getBrandName())
                .estTimeMinutes(entity.getEstTimeMinutes())
                .maintenancePrice(entity.getMaintenancePrice())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .parts(maintenanceCatalogModelPartService.getParts(entity.getId()))
                .build();
    }

    private CatalogModelResponse toResponse(MaintenanceCatalogModel entity) {
        return toResponse(entity, false);
    }
}

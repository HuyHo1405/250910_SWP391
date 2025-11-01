package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.MaintenanceCatalogModelPartRequest;
import com.example.demo.model.dto.MaintenanceCatalogModelPartResponse;
import com.example.demo.model.entity.MaintenanceCatalog;
import com.example.demo.model.entity.MaintenanceCatalogModelPart;
import com.example.demo.model.entity.Part;
import com.example.demo.model.entity.VehicleModel;
import com.example.demo.repo.*;
import com.example.demo.service.interfaces.IMaintenanceCatalogModelPartService;
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
public class MaintenanceCatalogModelPartService implements IMaintenanceCatalogModelPartService {

    private final AccessControlService accessControlService;

    private final MaintenanceCatalogModelPartRepo catalogModelPartRepo;
    private final MaintenanceCatalogRepo catalogRepo;
    private final VehicleModelRepo vehicleModelRepo;
    private final PartRepo partRepo;

    @Override
    @Transactional
    public List<MaintenanceCatalogModelPartResponse> syncBatch(Long catalogId, Long modelId, List<MaintenanceCatalogModelPartRequest> requests) {
        // Validate catalog & model tồn tại
        MaintenanceCatalog catalog = catalogRepo.findById(catalogId)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", catalogId));
        VehicleModel model = vehicleModelRepo.findById(modelId)
                .orElseThrow(() -> new CommonException.NotFound("Mẫu xe với Id", modelId));

        accessControlService.verifyCanAccessAllResources("MAINTENANCE_SERVICE", "update");

        // Lấy oldList các part hiện tại
        List<MaintenanceCatalogModelPart> oldList =
                catalogModelPartRepo.findByMaintenanceCatalogIdAndVehicleModelId(catalogId, modelId);

        // Tính delta add/update/delete
        SyncDelta delta = calculateDelta(catalog, model, oldList, requests);

        // Thực thi batch add/update/delete
        executeBatchOperations(delta);

        // Return danh sách part mới
        return catalogModelPartRepo.findByMaintenanceCatalogIdAndVehicleModelId(catalogId, modelId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteBatch(Long catalogId) {
        catalogRepo.findById(catalogId)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", catalogId));

        accessControlService.verifyCanAccessAllResources("MAINTENANCE_SERVICE", "delete");
        catalogModelPartRepo.deleteAllByMaintenanceCatalogId(catalogId);
    }

    @Override
    public MaintenanceCatalogModelPartResponse updateByIds(Long catalogId, Long modelId, Long partId, MaintenanceCatalogModelPartRequest request) {
        catalogRepo.findById(catalogId)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", catalogId));
        vehicleModelRepo.findById(modelId)
                .orElseThrow(() -> new CommonException.NotFound("Mẫu xe với Id", modelId));
        partRepo.findById(partId)
                .orElseThrow(() -> new CommonException.NotFound("Linh kiện với Id", partId));

        accessControlService.verifyCanAccessAllResources("MAINTENANCE_SERVICE", "update");

        MaintenanceCatalogModelPart entity = catalogModelPartRepo
                .findByMaintenanceCatalogIdAndVehicleModelIdAndPartId(catalogId, modelId, partId)
                .orElseThrow(() -> new CommonException.NotFound(
                        "Dịch vụ với Id: " + catalogId +
                                " cho mẫu xe Id: " + partId +
                                " với linh kiện Id: " + partId));
        updateFields(entity, request);
        return toResponse(catalogModelPartRepo.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceCatalogModelPartResponse findByIds(Long catalogId, Long modelId, Long partId) {
        catalogRepo.findById(catalogId)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", catalogId));
        vehicleModelRepo.findById(modelId)
                .orElseThrow(() -> new CommonException.NotFound("Mẫu xe với Id", modelId));
        partRepo.findById(partId)
                .orElseThrow(() -> new CommonException.NotFound("Linh kiện với Id", partId));

        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");

        MaintenanceCatalogModelPart entity = catalogModelPartRepo
                .findByMaintenanceCatalogIdAndVehicleModelIdAndPartId(catalogId, modelId, partId)
                .orElseThrow(() -> new CommonException.NotFound(
                        "Dịch vụ với Id: " + catalogId +
                                " cho mẫu xe Id: " + partId +
                                " với linh kiện Id: " + partId));

        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceCatalogModelPartResponse> getParts(Long catalogId, Long modelId) {
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");
        return catalogModelPartRepo.findByMaintenanceCatalogIdAndVehicleModelId(catalogId, modelId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // -------- Helper methods & DTO transformation --------
    private SyncDelta calculateDelta(MaintenanceCatalog catalog, VehicleModel model,
                                     List<MaintenanceCatalogModelPart> oldList,
                                     List<MaintenanceCatalogModelPartRequest> requests) {
        Map<Long, MaintenanceCatalogModelPart> oldMap = oldList.stream()
                .collect(Collectors.toMap(e -> e.getPart().getId(), e -> e));
        Map<Long, MaintenanceCatalogModelPartRequest> newMap = requests.stream()
                .collect(Collectors.toMap(MaintenanceCatalogModelPartRequest::getPartId, r -> r));

        List<MaintenanceCatalogModelPart> toAdd = new ArrayList<>();
        List<MaintenanceCatalogModelPart> toUpdate = new ArrayList<>();
        List<Long> toDeleteIds = new ArrayList<>();

        for (MaintenanceCatalogModelPart oldItem : oldList) {
            Long partId = oldItem.getPart().getId();
            MaintenanceCatalogModelPartRequest newItem = newMap.get(partId);
            if (newItem == null) {
                toDeleteIds.add(oldItem.getId());
            } else {
                if (hasChanges(oldItem, newItem)) {
                    updateFields(oldItem, newItem);
                    toUpdate.add(oldItem);
                }
                newMap.remove(partId);
            }
        }
        for (MaintenanceCatalogModelPartRequest dto : newMap.values()) {
            toAdd.add(createNewEntity(catalog, model, dto));
        }
        return new SyncDelta(toAdd, toUpdate, toDeleteIds);
    }

    private boolean hasChanges(MaintenanceCatalogModelPart oldItem, MaintenanceCatalogModelPartRequest newItem) {
        return !Objects.equals(oldItem.getQuantityRequired(), newItem.getQuantityRequired())
                || !Objects.equals(oldItem.getIsOptional(), newItem.getIsOptional())
                || !Objects.equals(oldItem.getNotes(), newItem.getNotes());
    }

    private void updateFields(MaintenanceCatalogModelPart entity, MaintenanceCatalogModelPartRequest dto) {
        entity.setQuantityRequired(dto.getQuantityRequired());
        entity.setIsOptional(dto.getIsOptional());
        entity.setNotes(dto.getNotes());
    }

    private MaintenanceCatalogModelPart createNewEntity(MaintenanceCatalog catalog, VehicleModel model, MaintenanceCatalogModelPartRequest dto) {
        Part part = partRepo.findById(dto.getPartId())
                .orElseThrow(() -> new CommonException.NotFound("Part with Id", dto.getPartId()));
        return MaintenanceCatalogModelPart.builder()
                .maintenanceCatalog(catalog)
                .vehicleModel(model)
                .part(part)
                .quantityRequired(dto.getQuantityRequired())
                .isOptional(dto.getIsOptional())
                .notes(dto.getNotes())
                .build();
    }

    private void executeBatchOperations(SyncDelta delta) {
        if (!delta.toAdd.isEmpty()) catalogModelPartRepo.saveAll(delta.toAdd);
        if (!delta.toUpdate.isEmpty()) catalogModelPartRepo.saveAll(delta.toUpdate);
        if (!delta.toDeleteIds.isEmpty()) catalogModelPartRepo.deleteAllByIdInBatch(delta.toDeleteIds);
    }

    @Value
    private static class SyncDelta {
        List<MaintenanceCatalogModelPart> toAdd;
        List<MaintenanceCatalogModelPart> toUpdate;
        List<Long> toDeleteIds;
    }

    private MaintenanceCatalogModelPartResponse toResponse(MaintenanceCatalogModelPart entity) {
        return MaintenanceCatalogModelPartResponse.builder()
                .partId(entity.getPart().getId())
                .partName(entity.getPart().getName())
                .quantityRequired(entity.getQuantityRequired())
                .isOptional(entity.getIsOptional())
                .notes(entity.getNotes())
                .build();
    }
}

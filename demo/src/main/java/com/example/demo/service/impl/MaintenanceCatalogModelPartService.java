package com.example.demo.service.impl;

import com.example.demo.exception.CommonException;
import com.example.demo.model.dto.CatalogModelPartRequest;
import com.example.demo.model.dto.CatalogModelPartResponse;
import com.example.demo.model.entity.*;
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

    private final MaintenanceCatalogModelRepo catalogModelRepo;
    private final MaintenanceCatalogModelPartRepo catalogModelPartRepo;
    private final MaintenanceCatalogRepo catalogRepo;
    private final PartRepo partRepo;

    @Override
    @Transactional
    public List<CatalogModelPartResponse> syncBatch(Long catalogModelId, List<CatalogModelPartRequest> requests) {
        // Validate catalog & model tồn tại
        MaintenanceCatalogModel catalogModel = catalogModelRepo.findById(catalogModelId)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ cho xe ", catalogModelId));

        // Lấy oldList các part hiện tại
        List<MaintenanceCatalogModelPart> oldList =
                catalogModelPartRepo.findByMaintenanceCatalogModelId(catalogModelId);

        // Tính delta add/update/delete
        SyncDelta delta = calculateDelta(catalogModel, oldList, requests);

        // Thực thi batch add/update/delete
        executeBatchOperations(delta);

        // Return danh sách part mới
        return catalogModelPartRepo.findByMaintenanceCatalogModelId(catalogModelId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteBatch(Long catalogId) {
        catalogRepo.findById(catalogId)
                .orElseThrow(() -> new CommonException.NotFound("Dịch vụ với Id", catalogId));
        List<MaintenanceCatalogModel> models = catalogModelRepo.findByMaintenanceCatalogId(catalogId);

        for (MaintenanceCatalogModel model : models) {
            catalogModelPartRepo.deleteAllByMaintenanceCatalogModelId(model.getId());
        }
    }

    @Override
    public CatalogModelPartResponse updateByIds(Long catalogModelId, Long partId, CatalogModelPartRequest request) {
        catalogModelRepo.findById(catalogModelId)
                .orElseThrow(() -> new CommonException.NotFound("Catalog Model với Id", catalogModelId));
        partRepo.findById(partId)
                .orElseThrow(() -> new CommonException.NotFound("Linh kiện với Id", partId));


        MaintenanceCatalogModelPart entity = catalogModelPartRepo
                .findByMaintenanceCatalogModelIdAndPartId(catalogModelId, partId)
                .orElseThrow(() -> new CommonException.NotFound(
                        "Catalog Model với Id: " + catalogModelId +
                                " với linh kiện Id: " + partId
                ));
        updateFields(entity, request);
        return toResponse(catalogModelPartRepo.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogModelPartResponse findByIds(Long catalogModelId, Long partId) {
        catalogModelRepo.findById(catalogModelId)
                .orElseThrow(() -> new CommonException.NotFound("Catalog Model với Id", catalogModelId));
        partRepo.findById(partId)
                .orElseThrow(() -> new CommonException.NotFound("Linh kiện với Id", partId));


        MaintenanceCatalogModelPart entity = catalogModelPartRepo
                .findByMaintenanceCatalogModelIdAndPartId(catalogModelId, partId)
                .orElseThrow(() -> new CommonException.NotFound(
                        "Catalog Model với Id: " + catalogModelId +
                                " với linh kiện Id: " + partId
                ));

        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogModelPartResponse> getParts(Long catalogModelId) {
        return catalogModelPartRepo.findByMaintenanceCatalogModelId(catalogModelId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // -------- Helper methods & DTO transformation --------
    private SyncDelta calculateDelta(MaintenanceCatalogModel catalogModel,
                                     List<MaintenanceCatalogModelPart> oldList,
                                     List<CatalogModelPartRequest> requests) {
        Map<Long, MaintenanceCatalogModelPart> oldMap = oldList.stream()
                .collect(Collectors.toMap(e -> e.getPart().getId(), e -> e));
        Map<Long, CatalogModelPartRequest> newMap = requests.stream()
                .collect(Collectors.toMap(CatalogModelPartRequest::getPartId, r -> r));

        List<MaintenanceCatalogModelPart> toAdd = new ArrayList<>();
        List<MaintenanceCatalogModelPart> toUpdate = new ArrayList<>();
        List<Long> toDeleteIds = new ArrayList<>();

        for (MaintenanceCatalogModelPart oldItem : oldList) {
            Long partId = oldItem.getPart().getId();
            CatalogModelPartRequest newItem = newMap.get(partId);
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
        for (CatalogModelPartRequest dto : newMap.values()) {
            toAdd.add(createNewEntity(catalogModel , dto));
        }
        return new SyncDelta(toAdd, toUpdate, toDeleteIds);
    }

    private boolean hasChanges(MaintenanceCatalogModelPart oldItem, CatalogModelPartRequest newItem) {
        return !Objects.equals(oldItem.getQuantityRequired(), newItem.getQuantityRequired())
                || !Objects.equals(oldItem.getIsOptional(), newItem.getIsOptional())
                || !Objects.equals(oldItem.getNotes(), newItem.getNotes());
    }

    private void updateFields(MaintenanceCatalogModelPart entity, CatalogModelPartRequest dto) {
        entity.setQuantityRequired(dto.getQuantityRequired());
        entity.setIsOptional(dto.getIsOptional());
        entity.setNotes(dto.getNotes());
    }

    private MaintenanceCatalogModelPart createNewEntity(MaintenanceCatalogModel catalog, CatalogModelPartRequest dto) {
        Part part = partRepo.findById(dto.getPartId())
                .orElseThrow(() -> new CommonException.NotFound("Linh kiện với Id", dto.getPartId()));
        return MaintenanceCatalogModelPart.builder()
                .maintenanceCatalogModel(catalog)
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

    private CatalogModelPartResponse toResponse(MaintenanceCatalogModelPart entity) {
        return CatalogModelPartResponse.builder()
                .partId(entity.getPart().getId())
                .partName(entity.getPart().getName())
                .quantityRequired(entity.getQuantityRequired())
                .isOptional(entity.getIsOptional())
                .notes(entity.getNotes())
                .build();
    }
}

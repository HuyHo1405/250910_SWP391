package com.example.demo.service.interfaces;

import com.example.demo.model.dto.MaintenanceCatalogModelPartRequest;
import com.example.demo.model.dto.MaintenanceCatalogModelPartResponse;
import io.micrometer.common.lang.Nullable;

import java.util.List;

public interface IMaintenanceCatalogModelPartService {

    MaintenanceCatalogModelPartResponse create(MaintenanceCatalogModelPartRequest request);

    List<MaintenanceCatalogModelPartResponse> createBatch(List<MaintenanceCatalogModelPartRequest> requests);

    List<MaintenanceCatalogModelPartResponse> findByCatalogAndModel(
            Long catalogId,
            Long modelId,
            @Nullable Long partId // Nếu truyền thì lấy đúng part này trong liên kết, không thì lấy tất cả
    );

    MaintenanceCatalogModelPartResponse update(Long catalogId, Long modelId, Long partId, MaintenanceCatalogModelPartRequest request);

    void delete(Long catalogId, Long modelId, Long partId);

    void deleteBatch(Long catalogId, Long modelId); // Xoá toàn bộ part của một catalog-model
}

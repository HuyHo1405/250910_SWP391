package com.example.demo.service.interfaces;

import com.example.demo.model.dto.MaintenanceCatalogModelRequest;
import com.example.demo.model.dto.MaintenanceCatalogModelResponse;
import io.micrometer.common.lang.Nullable;

import java.util.List;

public interface IMaintenanceCatalogModelService {

    MaintenanceCatalogModelResponse create(MaintenanceCatalogModelRequest request);

    List<MaintenanceCatalogModelResponse> createBatch(List<MaintenanceCatalogModelRequest> requests);

    List<MaintenanceCatalogModelResponse> findByCatalogId(
            Long catalogId,
            @Nullable Long modelId,         // Nếu muốn filter thêm 1 model cụ thể
            boolean includeParts            // Có include các part liên quan không
    );

    MaintenanceCatalogModelResponse updateByCatalogAndModel(Long catalogId, Long modelId, MaintenanceCatalogModelRequest request);

    void delete(Long catalogId, Long modelId);

    void deleteBatch(long catalogId);
}

package com.example.demo.service.interfaces;

import com.example.demo.model.dto.MaintenanceCatalogModelPartRequest;
import com.example.demo.model.dto.MaintenanceCatalogModelPartResponse;

import java.util.List;

public interface IMaintenanceCatalogModelPartService {

    List<MaintenanceCatalogModelPartResponse> syncBatch(Long catalogId, Long modelId, List<MaintenanceCatalogModelPartRequest> requests);

    void deleteBatch(Long catalogId);

    MaintenanceCatalogModelPartResponse updateByIds(Long catalogId, Long modelId, Long partId, MaintenanceCatalogModelPartRequest request);

    MaintenanceCatalogModelPartResponse findByIds(Long catalogId, Long modelId, Long partId);

    List<MaintenanceCatalogModelPartResponse> getParts(Long catalogId, Long modelId);
}

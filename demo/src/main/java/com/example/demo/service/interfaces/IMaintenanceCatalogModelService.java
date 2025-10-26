package com.example.demo.service.interfaces;

import com.example.demo.model.dto.MaintenanceCatalogModelRequest;
import com.example.demo.model.dto.MaintenanceCatalogModelResponse;

import java.util.List;

public interface IMaintenanceCatalogModelService {

    List<MaintenanceCatalogModelResponse> syncBatch(Long catalogId, List<MaintenanceCatalogModelRequest> requests);

    MaintenanceCatalogModelResponse updateByIds(Long catalogId, Long modelId, MaintenanceCatalogModelRequest request);

    MaintenanceCatalogModelResponse findByIds(Long catalogId, Long modelId, boolean includeParts);

    List<MaintenanceCatalogModelResponse> getModels(Long catalogId);

}

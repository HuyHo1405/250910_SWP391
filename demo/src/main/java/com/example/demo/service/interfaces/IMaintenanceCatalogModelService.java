package com.example.demo.service.interfaces;

import com.example.demo.model.dto.CatalogModelRequest;
import com.example.demo.model.dto.CatalogModelResponse;

import java.util.List;

public interface IMaintenanceCatalogModelService {

    List<CatalogModelResponse> syncBatch(Long catalogId, List<CatalogModelRequest> requests);

    CatalogModelResponse updateByIds(Long catalogId, Long modelId, CatalogModelRequest request);

    CatalogModelResponse findByIds(Long catalogId, Long modelId, boolean includeParts);

    List<CatalogModelResponse> getModels(Long catalogId);

}

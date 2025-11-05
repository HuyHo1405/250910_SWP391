package com.example.demo.service.interfaces;

import com.example.demo.model.dto.CatalogModelPartRequest;
import com.example.demo.model.dto.CatalogModelPartResponse;

import java.util.List;

public interface IMaintenanceCatalogModelPartService {

    List<CatalogModelPartResponse> syncBatch(Long catalogModelId, List<CatalogModelPartRequest> requests);

    void deleteBatch(Long catalogId);

    CatalogModelPartResponse updateByIds(Long catalogModelId, Long partId, CatalogModelPartRequest request);

    CatalogModelPartResponse findByIds(Long catalogModelId, Long partId);

    List<CatalogModelPartResponse> getParts(Long catalogModelId);
}

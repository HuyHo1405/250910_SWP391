package com.example.demo.service.interfaces;

import com.example.demo.model.dto.CatalogRequest;
import com.example.demo.model.dto.CatalogResponse;
import com.example.demo.model.modelEnum.MaintenanceCatalogType;
import io.micrometer.common.lang.Nullable;

import java.util.List;

public interface IMaintenanceCatalogService {

    CatalogResponse create(CatalogRequest request);

    CatalogResponse update(Long id, CatalogRequest request);

    CatalogResponse findById(Long id);

    List<CatalogResponse> findAll(
            @Nullable MaintenanceCatalogType type,
            @Nullable String vin,
            boolean includeModels
    );

    void delete(Long id);

}

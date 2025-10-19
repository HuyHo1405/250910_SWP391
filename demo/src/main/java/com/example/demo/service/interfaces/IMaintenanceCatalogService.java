package com.example.demo.service.interfaces;

import com.example.demo.model.dto.MaintenanceCatalogRequest;
import com.example.demo.model.dto.MaintenanceCatalogResponse;
import com.example.demo.model.modelEnum.MaintenanceCatalogType;
import io.micrometer.common.lang.Nullable;

import java.util.List;

public interface IMaintenanceCatalogService {

    MaintenanceCatalogResponse create(MaintenanceCatalogRequest request);

    MaintenanceCatalogResponse update(Long id, MaintenanceCatalogRequest request);

    MaintenanceCatalogResponse findById(Long id, boolean includeModels);

    List<MaintenanceCatalogResponse> findAll(
            @Nullable MaintenanceCatalogType type,
            @Nullable String vin,
            boolean includeModels
    );

    void delete(Long id);

}

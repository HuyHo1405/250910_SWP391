package com.example.demo.service.interfaces;

import com.example.demo.model.dto.CatalogRequest;
import com.example.demo.model.dto.CatalogResponse;
import com.example.demo.model.dto.EnumSchemaResponse;
import com.example.demo.model.modelEnum.MaintenanceCatalogCategory;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMaintenanceCatalogService {

    CatalogResponse create(CatalogRequest request);

    CatalogResponse update(Long id, CatalogRequest request);

    CatalogResponse findById(Long id);

    Page<CatalogResponse> findAllPaged(
            @Nullable MaintenanceCatalogCategory type,
            @Nullable String vin,
            @Nullable Long modelId,
            Pageable pageable
    );

    void delete(Long id);

    EnumSchemaResponse getCategoryEnumSchema();

}

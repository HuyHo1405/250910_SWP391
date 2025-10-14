package com.example.demo.service.interfaces;

import com.example.demo.model.dto.MaintenanceCatalogRequest;
import com.example.demo.model.dto.MaintenanceCatalogResponse;
import java.util.List;

public interface IMaintenanceCatalogService {
    MaintenanceCatalogResponse createService(MaintenanceCatalogRequest dto);
    List<MaintenanceCatalogResponse> listServices();
    MaintenanceCatalogResponse getService(Long id);
    MaintenanceCatalogResponse updateService(Long id, MaintenanceCatalogRequest dto);
    void deleteService(Long id);
}

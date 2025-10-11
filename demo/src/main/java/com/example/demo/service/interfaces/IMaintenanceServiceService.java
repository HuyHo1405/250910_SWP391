package com.example.demo.service.interfaces;

import com.example.demo.model.dto.MaintenanceServiceRequest;
import com.example.demo.model.dto.MaintenanceServiceResponse;
import java.util.List;

public interface IMaintenanceServiceService {
    MaintenanceServiceResponse createService(MaintenanceServiceRequest dto);
    List<MaintenanceServiceResponse> listServices();
    MaintenanceServiceResponse getService(Long id);
    MaintenanceServiceResponse updateService(Long id, MaintenanceServiceRequest dto);
    void deleteService(Long id);
}

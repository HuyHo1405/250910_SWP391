package com.example.demo.service.impl;

import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.entity.MaintenanceCatalog;
import com.example.demo.exception.MaintenanceCatalogException;
import com.example.demo.model.dto.MaintenanceCatalogRequest;
import com.example.demo.model.dto.MaintenanceCatalogResponse;
import com.example.demo.repo.MaintenanceCatalogRepo;
import com.example.demo.service.interfaces.IMaintenanceCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceCatalogService implements IMaintenanceCatalogService {
    private final MaintenanceCatalogRepo serviceRepo;
    private final AccessControlService accessControlService;

    @Transactional
    @Override
    public MaintenanceCatalogResponse createService(MaintenanceCatalogRequest request) {
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "create");
        boolean exists = serviceRepo.findByName(request.getName()).isPresent();
        if (exists) throw new MaintenanceCatalogException.DuplicateServiceName(request.getName());

        MaintenanceCatalog s = MaintenanceCatalog.builder()
                .name(request.getName())
                .maintenanceServiceType(request.getMaintenanceServiceType())
                .description(request.getDescription())
                .estTimeMinutes(request.getEstTimeMinutes())
                .currentPrice(request.getCurrentPrice())
                .status(EntityStatus.ACTIVE)
                .build();
        MaintenanceCatalog saved = serviceRepo.save(s);
        return toDTO(saved);
    }

    @Override
    public List<MaintenanceCatalogResponse> listServices() {
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");
        return serviceRepo.findByStatus(EntityStatus.ACTIVE)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceCatalogResponse getService(Long id) {
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");
        MaintenanceCatalog s = serviceRepo.findByIdAndStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new MaintenanceCatalogException.ServiceInactive(id + ""));
        return toDTO(s);
    }

    @Transactional
    @Override
    public MaintenanceCatalogResponse updateService(Long id, MaintenanceCatalogRequest request) {
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "update");
        MaintenanceCatalog s = serviceRepo.findByIdAndStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new MaintenanceCatalogException.ServiceInactive(id + ""));

        var existed = serviceRepo.findByName(request.getName());
        if (existed.isPresent() && !existed.get().getId().equals(id))
            throw new MaintenanceCatalogException.DuplicateServiceName(request.getName());
        s.setName(request.getName());
        s.setDescription(request.getDescription());
        s.setMaintenanceServiceType(request.getMaintenanceServiceType());
        s.setEstTimeMinutes(request.getEstTimeMinutes());
        s.setCurrentPrice(request.getCurrentPrice());
        MaintenanceCatalog updated = serviceRepo.save(s);
        return toDTO(updated);
    }

    @Transactional
    @Override
    public void deleteService(Long id) {
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "delete");
        MaintenanceCatalog s = serviceRepo.findByIdAndStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new MaintenanceCatalogException.ServiceInactive(id + ""));
        s.setStatus(EntityStatus.INACTIVE);
        serviceRepo.save(s);
    }

    private MaintenanceCatalogResponse toDTO(MaintenanceCatalog s) {
        MaintenanceCatalogResponse dto = new MaintenanceCatalogResponse();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        dto.setMaintenanceServiceType(s.getMaintenanceServiceType());
        dto.setEstTimeMinutes(s.getEstTimeMinutes());
        dto.setCurrentPrice(s.getCurrentPrice());
        dto.setStatus(s.getStatus().name());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}

package com.example.demo.service.impl;

import com.example.demo.model.entity.EntityStatus;
import com.example.demo.model.entity.MaintenanceService;
import com.example.demo.exception.MaintenanceServiceException;
import com.example.demo.model.dto.MaintenanceServiceRequest;
import com.example.demo.model.dto.MaintenanceServiceResponse;
import com.example.demo.repo.MaintenanceServiceRepo;
import com.example.demo.service.interfaces.IMaintenanceServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceService implements IMaintenanceServiceService {
    private final MaintenanceServiceRepo serviceRepo;
    private final AccessControlService accessControlService;

    @Transactional
    @Override
    public MaintenanceServiceResponse createService(MaintenanceServiceRequest request) {
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "create");
        boolean exists = serviceRepo.findByName(request.getName()).isPresent();
        if (exists) throw new MaintenanceServiceException.DuplicateServiceName(request.getName());

        MaintenanceService s = MaintenanceService.builder()
                .name(request.getName())
                .description(request.getDescription())
                .estTimeHours(request.getEstTimeHours())
                .currentPrice(request.getCurrentPrice())
                .status(EntityStatus.ACTIVE)
                .build();
        MaintenanceService saved = serviceRepo.save(s);
        return toDTO(saved);
    }

    @Override
    public List<MaintenanceServiceResponse> listServices() {
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");
        return serviceRepo.findByStatus(EntityStatus.ACTIVE)
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaintenanceServiceResponse getService(Long id) {
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "read");
        MaintenanceService s = serviceRepo.findByIdAndStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new MaintenanceServiceException.ServiceInactive(id + ""));
        return toDTO(s);
    }

    @Transactional
    @Override
    public MaintenanceServiceResponse updateService(Long id, MaintenanceServiceRequest request) {
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "update");
        MaintenanceService s = serviceRepo.findByIdAndStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new MaintenanceServiceException.ServiceInactive(id + ""));

        var existed = serviceRepo.findByName(request.getName());
        if (existed.isPresent() && !existed.get().getId().equals(id))
            throw new MaintenanceServiceException.DuplicateServiceName(request.getName());

        s.setName(request.getName());
        s.setDescription(request.getDescription());
        s.setEstTimeHours(request.getEstTimeHours());
        s.setCurrentPrice(request.getCurrentPrice());
        MaintenanceService updated = serviceRepo.save(s);
        return toDTO(updated);
    }

    @Transactional
    @Override
    public void deleteService(Long id) {
        accessControlService.verifyResourceAccessWithoutOwnership("MAINTENANCE_SERVICE", "delete");
        MaintenanceService s = serviceRepo.findByIdAndStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new MaintenanceServiceException.ServiceInactive(id + ""));
        s.setStatus(EntityStatus.INACTIVE);
        serviceRepo.save(s);
    }

    private MaintenanceServiceResponse toDTO(MaintenanceService s) {
        MaintenanceServiceResponse dto = new MaintenanceServiceResponse();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        dto.setEstTimeHours(s.getEstTimeHours());
        dto.setCurrentPrice(s.getCurrentPrice());
        dto.setStatus(s.getStatus().name());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}

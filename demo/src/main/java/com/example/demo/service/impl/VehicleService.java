package com.example.demo.service.impl;

import com.example.demo.exception.UserException;
import com.example.demo.exception.VehicleException;
import com.example.demo.model.dto.VehicleRequest;
import com.example.demo.model.dto.VehicleResponse;
import com.example.demo.model.entity.EntityStatus;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.Vehicle;
import com.example.demo.model.entity.VehicleModel;
import com.example.demo.repo.UserRepo;
import com.example.demo.repo.VehicleModelRepo;
import com.example.demo.repo.VehicleRepo;
import com.example.demo.service.interfaces.IVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService implements IVehicleService {

    private final AccessControlService accessControlService;
    private final VehicleRepo vehicleRepo;
    private final UserRepo userRepo;
    private final VehicleModelRepo vehicleModelRepo;

    @Override
    @Transactional
    public VehicleResponse createVehicle(VehicleRequest.Create request) {
        // 1. Validate business rules
        validateVehicleUniqueness(request.getVin(), request.getPlateNumber());

        // 2. Check access permission
        accessControlService.verifyVehicleAccess(request.getUserId(), "create");

        // 3. Fetch and validate related entities
        VehicleModel vehicleModel = getVehicleModelOrThrow(request.getVehicleModelId());
        User user = getUserOrThrow(request.getUserId());

        // 4. Create and save vehicle
        Vehicle vehicle = buildVehicle(request, user, vehicleModel);
        return VehicleResponse.fromEntity(vehicleRepo.save(vehicle));
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(String vin, VehicleRequest.Update request) {
        // 1. Find vehicle
        Vehicle vehicle = getActiveVehicleOrThrow(vin);

        // 2. Check access permission
        accessControlService.verifyVehicleAccess(vehicle, "update");

        // 3. Update fields
        updateVehicleFields(vehicle, request);

        // 4. Save and return
        return VehicleResponse.fromEntity(vehicleRepo.save(vehicle));
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleByVin(String vin) {
        Vehicle vehicle = getActiveVehicleOrThrow(vin);
        accessControlService.verifyVehicleAccess(vehicle, "read");
        return VehicleResponse.fromEntity(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByUser(Long userId) {
        // Customer can only view their own vehicles
        // Staff/Admin can view any user's vehicles
        accessControlService.verifyVehicleAccess(userId, "read");

        return vehicleRepo.findByUserIdAndEntityStatus(userId, EntityStatus.ACTIVE)
                .stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {
        // Only Staff/Admin can view all vehicles
        accessControlService.verifyAdminOrStaffAccess();

        return vehicleRepo.findAllByEntityStatus(EntityStatus.ACTIVE)
                .stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VehicleResponse deleteVehicle(String vin) {
        Vehicle vehicle = getActiveVehicleOrThrow(vin);
        accessControlService.verifyVehicleAccess(vehicle, "delete");

        vehicleRepo.softDelete(vin);
        vehicle.setEntityStatus(EntityStatus.INACTIVE);

        return VehicleResponse.fromEntity(vehicle);
    }

    // === Private Helper Methods ===
    private void validateVehicleUniqueness(String vin, String plateNumber) {
        if (vehicleRepo.existsByVinAndEntityStatus(vin, EntityStatus.ACTIVE)) {
            throw new VehicleException.VehicleAlreadyExists(vin);
        }
        if (vehicleRepo.existsByPlateNumberAndEntityStatus(plateNumber, EntityStatus.ACTIVE)) {
            throw new VehicleException.DuplicatePlateNumber(plateNumber);
        }
    }

    private Vehicle getActiveVehicleOrThrow(String vin) {
        return vehicleRepo.findByVinAndEntityStatus(vin, EntityStatus.ACTIVE)
                .orElseThrow(() -> new VehicleException.VehicleNotFound(vin));
    }

    private User getUserOrThrow(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(UserException.UserNotFound::new);
    }

    private VehicleModel getVehicleModelOrThrow(Long modelId) {
        return vehicleModelRepo.findById(modelId)
                .orElseThrow(() -> new VehicleException.InvalidVehicleModel(modelId));
    }

    private Vehicle buildVehicle(VehicleRequest.Create request, User user, VehicleModel model) {
        return Vehicle.builder()
                .vin(request.getVin())
                .name(request.getName())
                .plateNumber(request.getPlateNumber())
                .year(request.getYear())
                .color(request.getColor())
                .distanceTraveledKm(request.getDistanceTraveledKm())
                .purchasedAt(request.getPurchasedAt())
                .createdAt(LocalDateTime.now())
                .user(user)
                .model(model)
                .entityStatus(EntityStatus.ACTIVE)
                .build();
    }

    private void updateVehicleFields(Vehicle vehicle, VehicleRequest.Update request) {
        if (request.getName() != null) {
            vehicle.setName(request.getName());
        }

        if (request.getPlateNumber() != null && !request.getPlateNumber().equals(vehicle.getPlateNumber())) {
            validatePlateNumberUniqueness(request.getPlateNumber());
            vehicle.setPlateNumber(request.getPlateNumber());
        }

        if (request.getYear() != null) {
            vehicle.setYear(request.getYear());
        }

        if (request.getColor() != null) {
            vehicle.setColor(request.getColor());
        }

        if (request.getDistanceTraveledKm() != null) {
            vehicle.setDistanceTraveledKm(request.getDistanceTraveledKm());
        }

        if (request.getPurchasedAt() != null) {
            vehicle.setPurchasedAt(request.getPurchasedAt());
        }

        if (request.getVehicleModelId() != null) {
            VehicleModel newModel = getVehicleModelOrThrow(request.getVehicleModelId());
            vehicle.setModel(newModel);
        }
    }

    private void validatePlateNumberUniqueness(String plateNumber) {
        if (vehicleRepo.existsByPlateNumberAndEntityStatus(plateNumber, EntityStatus.ACTIVE)) {
            throw new VehicleException.DuplicatePlateNumber(plateNumber);
        }
    }
}
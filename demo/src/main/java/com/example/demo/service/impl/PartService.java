package com.example.demo.service.impl;

import com.example.demo.exception.PartException;
import com.example.demo.model.dto.PartRequest;
import com.example.demo.model.dto.PartResponse;
import com.example.demo.model.entity.Part;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.repo.PartRepo;
import com.example.demo.service.interfaces.IPartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PartService implements IPartService {

    private final PartRepo partRepository;
    private final AccessControlService accessControlService;

    // ================================
    // CREATE
    // ================================

    @Override
    public PartResponse createPart(PartRequest request) {
        log.info("Creating new part: {}", request.getName());
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "create");
        if (partRepository.existsByPartNumber(request.getPartNumber())) {
            throw new PartException.PartNumberExists(request.getPartNumber());
        }
        Part part = Part.builder()
                .name(request.getName())
                .partNumber(request.getPartNumber())
                .manufacturer(request.getManufacturer())
                .description(request.getDescription())
                .currentUnitPrice(request.getCurrentUnitPrice())
                .quantity(request.getQuantity())
                .reserved(request.getReserved())
                .status(EntityStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        Part savedPart = partRepository.save(part);
        log.info("Part created successfully with ID: {}", savedPart.getId());
        return mapToResponse(savedPart);
    }

    // ================================
    // READ
    // ================================

    @Override
    @Transactional(readOnly = true)
    public PartResponse getPartById(Long id) {
        log.info("Fetching part by ID: {}", id);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "read");
        Part part = findPartById(id);
        return mapToResponse(part);
    }

    @Override
    @Transactional(readOnly = true)
    public PartResponse getPartByPartNumber(String partNumber) {
        log.info("Fetching part by part number: {}", partNumber);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "read");
        Part part = partRepository.findByPartNumber(partNumber)
                .orElseThrow(() -> new PartException.PartNumberNotFound(partNumber));
        return mapToResponse(part);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartResponse> getLowStockParts(BigDecimal threshold) {
        log.info("Fetching low stock parts with threshold: {}", threshold);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "read");
        return partRepository.findByQuantityLessThanEqual(threshold).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartResponse> getAllPartsFiltered(String manufacturer, EntityStatus status, String searchKeyword) {
        log.info("Fetching parts with filters - manufacturer: {}, status: {}, search: {}", manufacturer, status, searchKeyword);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "read");
        List<Part> parts = partRepository.findAll();

        if (manufacturer != null && !manufacturer.trim().isEmpty()) {
            parts = parts.stream()
                    .filter(p -> p.getManufacturer().equalsIgnoreCase(manufacturer))
                    .collect(Collectors.toList());
        }
        if (status != null) {
            parts = parts.stream()
                    .filter(p -> p.getStatus() == status)
                    .collect(Collectors.toList());
        }
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String keyword = searchKeyword.toLowerCase();
            parts = parts.stream()
                    .filter(p -> p.getName().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
        }
        log.info("Found {} parts matching filters", parts.size());
        return parts.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ================================
    // UPDATE
    // ================================

    @Override
    public PartResponse updatePart(Long id, PartRequest request) {
        log.info("Updating part with ID: {}", id);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "update");
        Part part = findPartById(id);

        if (!part.getPartNumber().equals(request.getPartNumber())
                && partRepository.existsByPartNumber(request.getPartNumber())) {
            throw new PartException.PartNumberExists(request.getPartNumber());
        }

        part.setName(request.getName());
        part.setPartNumber(request.getPartNumber());
        part.setManufacturer(request.getManufacturer());
        part.setDescription(request.getDescription());
        part.setCurrentUnitPrice(request.getCurrentUnitPrice());
        part.setQuantity(request.getQuantity());
        part.setReserved(request.getReserved());

        Part updatedPart = partRepository.save(part);
        log.info("Part updated successfully: {}", id);
        return mapToResponse(updatedPart);
    }

    @Override
    public PartResponse updatePartPrice(Long id, BigDecimal newPrice) {
        log.info("Updating price for part ID: {} to {}", id, newPrice);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "update");
        Part part = findPartById(id);

        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new PartException.InvalidPrice();
        }
        part.setCurrentUnitPrice(newPrice);
        Part updatedPart = partRepository.save(part);
        return mapToResponse(updatedPart);
    }

    @Override
    public PartResponse updatePartStatus(Long id, EntityStatus status) {
        log.info("Updating status for part ID: {} to {}", id, status);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "update");
        Part part = findPartById(id);
        part.setStatus(status);
        Part updatedPart = partRepository.save(part);
        return mapToResponse(updatedPart);
    }

    // ================================
    // INVENTORY (Adjust stock/methods dùng BigDecimal)
    // ================================

    @Override
    public PartResponse adjustPartStock(Long id, BigDecimal adjustment) {
        log.info("Adjusting stock for part ID: {} by {}", id, adjustment);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "manage_stock");
        Part part = findPartById(id);
        BigDecimal currentQuantity = part.getQuantity();
        BigDecimal newQuantity = currentQuantity.add(adjustment);
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new PartException.NegativeQuantityResult(currentQuantity, adjustment);
        }
        part.setQuantity(newQuantity);
        Part updatedPart = partRepository.save(part);
        return mapToResponse(updatedPart);
    }

    @Override
    public PartResponse adjustReservedStock(Long id, BigDecimal adjustment) {
        log.info("Adjusting reserved for part ID: {} by {}", id, adjustment);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "manage_reserved");
        Part part = findPartById(id);
        BigDecimal currentReserved = part.getReserved();
        BigDecimal newReserved = currentReserved.add(adjustment);
        if (newReserved.compareTo(BigDecimal.ZERO) < 0) {
            throw new PartException.NegativeQuantityResult(currentReserved, adjustment);
        }
        part.setReserved(newReserved);
        Part updatedPart = partRepository.save(part);
        return mapToResponse(updatedPart);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAvailability(Long id, BigDecimal requiredQuantity) {
        log.info("Checking availability for part ID: {} with required quantity: {}", id, requiredQuantity);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "read");
        Part part = findPartById(id);
        boolean available = part.getQuantity().compareTo(requiredQuantity) >= 0
                && part.getStatus() == EntityStatus.ACTIVE;
        log.info("Part {} availability: {}", id, available);
        return available;
    }

    // ================================
    // DELETE/DEACTIVATE
    // ================================

    @Override
    public void deletePart(Long id) {
        log.info("Deleting part with ID: {}", id);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "delete");
        Part part = findPartById(id);
        partRepository.delete(part);
        log.info("Part deleted successfully: {}", id);
    }

    @Override
    public PartResponse deactivatePart(Long id) {
        log.info("Deactivating part with ID: {}", id);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "update");
        Part part = findPartById(id);
        if (part.getStatus() == EntityStatus.INACTIVE) {
            throw new PartException.PartAlreadyInactive(id);
        }
        return updatePartStatus(id, EntityStatus.INACTIVE);
    }

    @Override
    public PartResponse reactivatePart(Long id) {
        log.info("Reactivating part with ID: {}", id);
        accessControlService.verifyResourceAccessWithoutOwnership("PART", "update");
        Part part = findPartById(id);
        if (part.getStatus() == EntityStatus.ACTIVE) {
            throw new PartException.PartAlreadyActive(id);
        }
        return updatePartStatus(id, EntityStatus.ACTIVE);
    }

    // ================================
    // HELPER METHODS
    // ================================

    private Part findPartById(Long id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new PartException.PartNotFound(id));
    }

    private PartResponse mapToResponse(Part part) {
        return PartResponse.builder()
                .id(part.getId())
                .name(part.getName())
                .partNumber(part.getPartNumber())
                .manufacturer(part.getManufacturer())
                .description(part.getDescription())
                .currentUnitPrice(part.getCurrentUnitPrice())
                .quantity(part.getQuantity())
                .reserved(part.getReserved())
                .status(part.getStatus().name())
                .createdAt(part.getCreatedAt())
                .build();
    }
}

package com.example.demo.controller;

import com.example.demo.model.dto.PartRequest;
import com.example.demo.model.dto.PartResponse;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.service.interfaces.IPartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
@Tag(name = "Part Management")
public class PartController {

    private final IPartService partService;

    @PostMapping
    public ResponseEntity<PartResponse> createPart(@Valid @RequestBody PartRequest request) {
        PartResponse response = partService.createPart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> getPartById(@PathVariable Long id) {
        PartResponse response = partService.getPartById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/part-number/{partNumber}")
    public ResponseEntity<PartResponse> getPartByPartNumber(@PathVariable Integer partNumber) {
        PartResponse response = partService.getPartByPartNumber(partNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PartResponse>> getAllParts(
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) EntityStatus status,
            @RequestParam(required = false) String search) {
        List<PartResponse> parts = partService.getAllPartsFiltered(manufacturer, status, search);
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<PartResponse>> getLowStockParts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<PartResponse> parts = partService.getLowStockParts(threshold);
        return ResponseEntity.ok(parts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody PartRequest request) {
        PartResponse response = partService.updatePart(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<PartResponse> updatePartPrice(
            @PathVariable Long id,
            @RequestParam Double newPrice) {
        PartResponse response = partService.updatePartPrice(id, newPrice);
        return ResponseEntity.ok(response);
    }

    // ================================
    // XÓA: /quantity, /adjust-stock
    // CHỈ GIỮ 2 ENDPOINTS QUAN TRỌNG:
    // ================================

    @PostMapping("/{id}/stock/increase")
    public ResponseEntity<PartResponse> increaseStock(
            @PathVariable Long id,
            @RequestParam Integer amount) {
        PartResponse response = partService.increaseStock(id, amount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/stock/decrease")
    public ResponseEntity<PartResponse> decreaseStock(
            @PathVariable Long id,
            @RequestParam Integer amount) {
        PartResponse response = partService.decreaseStock(id, amount);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<PartResponse> deactivatePart(@PathVariable Long id) {
        PartResponse response = partService.deactivatePart(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<PartResponse> reactivatePart(@PathVariable Long id) {
        PartResponse response = partService.reactivatePart(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/check-availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable Long id,
            @RequestParam Integer requiredQuantity) {
        boolean available = partService.checkAvailability(id, requiredQuantity);
        return ResponseEntity.ok(available);
    }
}


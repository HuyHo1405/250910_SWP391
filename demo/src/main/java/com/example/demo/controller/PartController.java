package com.example.demo.controller;

import com.example.demo.model.dto.ErrorResponse;
import com.example.demo.model.dto.PartRequest;
import com.example.demo.model.dto.PartResponse;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.service.interfaces.IPartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public ResponseEntity<PartResponse> getPartByPartNumber(@PathVariable String partNumber) {
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
            @RequestParam(defaultValue = "10") BigDecimal threshold) {
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

    // ================================
    // XÓA: /quantity, /adjust-stock
    // CHỈ GIỮ 2 ENDPOINTS QUAN TRỌNG:
    // ================================

    @PostMapping("/{id}/stock/increase")
    public ResponseEntity<PartResponse> increaseStock(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        PartResponse response = partService.adjustPartStock(id, amount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/stock/decrease")
    public ResponseEntity<PartResponse> decreaseStock(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        PartResponse response = partService.adjustPartStock(id, amount.negate());
        return ResponseEntity.ok(response);
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

}


package com.example.demo.controller;

import com.example.demo.model.dto.PartRequest;
import com.example.demo.model.dto.PartResponse;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.service.interfaces.IPartService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Part")
public class PartController {

    private final IPartService partService;

    @PostMapping
    @Operation(summary = "[PRIVATE] [STAFF] Create part", description = "Allows staff to create a new part.")
    public ResponseEntity<PartResponse> createPart(@Valid @RequestBody PartRequest request) {
        PartResponse response = partService.createPart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "[PRIVATE] [STAFF] Get part by id", description = "Returns part details by id. Requires authentication as staff.")
    public ResponseEntity<PartResponse> getPartById(@PathVariable Long id) {
        PartResponse response = partService.getPartById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/part-number/{partNumber}")
    @Operation(summary = "[PRIVATE] [STAFF] Get part by part number", description = "Returns part details by part number. Requires authentication as staff.")
    public ResponseEntity<PartResponse> getPartByPartNumber(@PathVariable String partNumber) {
        PartResponse response = partService.getPartByPartNumber(partNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "[PRIVATE] [STAFF] Get all parts (filtered)", description = "Returns all parts, optionally filtered by manufacturer, status, or search. Requires authentication as staff.")
    public ResponseEntity<List<PartResponse>> getAllParts(
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) EntityStatus status,
            @RequestParam(required = false) String search) {
        List<PartResponse> parts = partService.getAllPartsFiltered(manufacturer, status, search);
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "[PRIVATE] [STAFF] Get low stock parts", description = "Returns parts with stock below the given threshold. Requires authentication as staff.")
    public ResponseEntity<List<PartResponse>> getLowStockParts(
            @RequestParam(defaultValue = "10") BigDecimal threshold) {
        List<PartResponse> parts = partService.getLowStockParts(threshold);
        return ResponseEntity.ok(parts);
    }

    @PutMapping("/{id}")
    @Operation(summary = "[PRIVATE] [STAFF] Update part", description = "Allows staff to update part details by id.")
    public ResponseEntity<PartResponse> updatePart(
            @PathVariable Long id,
            @Valid @RequestBody PartRequest request) {
        PartResponse response = partService.updatePart(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/stock/increase")
    @Operation(summary = "[PRIVATE] [STAFF] Increase part stock", description = "Allows staff to increase the stock of a part by a given amount.")
    public ResponseEntity<PartResponse> increaseStock(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        PartResponse response = partService.adjustPartStock(id, amount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/stock/decrease")
    @Operation(summary = "[PRIVATE] [STAFF] Decrease part stock", description = "Allows staff to decrease the stock of a part by a given amount.")
    public ResponseEntity<PartResponse> decreaseStock(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        PartResponse response = partService.adjustPartStock(id, amount.negate());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "[PRIVATE] [STAFF] Deactivate part", description = "Allows staff to deactivate a part by id.")
    public ResponseEntity<PartResponse> deactivatePart(@PathVariable Long id) {
        PartResponse response = partService.deactivatePart(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "[PRIVATE] [STAFF] Reactivate part", description = "Allows staff to reactivate a part by id.")
    public ResponseEntity<PartResponse> reactivatePart(@PathVariable Long id) {
        PartResponse response = partService.reactivatePart(id);
        return ResponseEntity.ok(response);
    }

}

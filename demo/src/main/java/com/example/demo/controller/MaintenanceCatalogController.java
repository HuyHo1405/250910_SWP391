package com.example.demo.controller;

import com.example.demo.model.dto.*;
import com.example.demo.model.modelEnum.MaintenanceCatalogType;
import com.example.demo.service.interfaces.IMaintenanceCatalogService;
import com.example.demo.service.interfaces.IMaintenanceCatalogModelService;
import com.example.demo.service.interfaces.IMaintenanceCatalogModelPartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-catalogs")
@RequiredArgsConstructor
@Tag(name = "Maintenance Catalog")
public class MaintenanceCatalogController {

    private final IMaintenanceCatalogService catalogService;
    private final IMaintenanceCatalogModelService catalogModelService;
    private final IMaintenanceCatalogModelPartService catalogModelPartService;

    // Catalog CRUD -----------------------------------

    @GetMapping
    public ResponseEntity<List<MaintenanceCatalogResponse>> getAllCatalogs(
            @RequestParam(required = false) MaintenanceCatalogType type,
            @RequestParam(required = false) String vin,
            @RequestParam(defaultValue = "false") boolean includeModels
    ) {
        return ResponseEntity.ok(catalogService.findAll(type, vin, includeModels));
    }

    @PostMapping
    public ResponseEntity<MaintenanceCatalogResponse> createCatalog(
            @RequestBody @Valid MaintenanceCatalogRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceCatalogResponse> updateCatalog(
            @PathVariable Long id,
            @RequestBody @Valid MaintenanceCatalogRequest request
    ) {
        return ResponseEntity.ok(catalogService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceCatalogResponse> getCatalogById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean includeModels
    ) {
        return ResponseEntity.ok(catalogService.findById(id, includeModels));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatalog(@PathVariable Long id) {
        catalogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Catalog Model CRUD -----------------------------------

    @PutMapping("/{catalogId}/models/sync")
    public ResponseEntity<List<MaintenanceCatalogModelResponse>> syncBatch(
            @PathVariable Long catalogId,
            @RequestBody List<MaintenanceCatalogModelRequest> requests
    ) {
        return ResponseEntity.ok(catalogModelService.syncBatch(catalogId, requests));
    }

    @PutMapping("/{catalogId}/models/{modelId}")
    public ResponseEntity<MaintenanceCatalogModelResponse> updateByIds(
            @PathVariable Long catalogId,
            @PathVariable Long modelId,
            @RequestBody MaintenanceCatalogModelRequest request
    ) {
        return ResponseEntity.ok(catalogModelService.updateByIds(catalogId, modelId, request));
    }

    @GetMapping("/{catalogId}/models/{modelId}")
    public ResponseEntity<MaintenanceCatalogModelResponse> findByIds(
            @PathVariable Long catalogId,
            @PathVariable Long modelId,
            @RequestParam(defaultValue = "false") boolean includeParts
    ) {
        return ResponseEntity.ok(catalogModelService.findByIds(catalogId, modelId, includeParts));
    }

    // Catalog Model Part CRUD -----------------------------------

    @PutMapping("/{catalogId}/models/{modelId}/parts/sync")
    public ResponseEntity<List<MaintenanceCatalogModelPartResponse>> syncBatch(
            @PathVariable Long catalogId,
            @PathVariable Long modelId,
            @RequestBody List<MaintenanceCatalogModelPartRequest> requests
    ) {
        return ResponseEntity.ok(catalogModelPartService.syncBatch(catalogId, modelId, requests));
    }

    @PutMapping("/{catalogId}/models/{modelId}/parts/{partId}")
    public ResponseEntity<MaintenanceCatalogModelPartResponse> updateByIds(
            @PathVariable Long catalogId,
            @PathVariable Long modelId,
            @PathVariable Long partId,
            @RequestBody MaintenanceCatalogModelPartRequest request
    ) {
        return ResponseEntity.ok(catalogModelPartService.updateByIds(catalogId, modelId, partId, request));
    }

    @GetMapping("/{catalogId}/models/{modelId}/parts/{partId}")
    public ResponseEntity<MaintenanceCatalogModelPartResponse> findByIds(
            @PathVariable Long catalogId,
            @PathVariable Long modelId,
            @PathVariable Long partId
    ) {
        return ResponseEntity.ok(catalogModelPartService.findByIds(catalogId, modelId, partId));
    }
}

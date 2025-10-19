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
@Tag(name = "Maintenance Catalog", description = "APIs quản lý catalog dịch vụ bảo dưỡng")
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

    @PostMapping("/{catalogId}/models")
    public ResponseEntity<MaintenanceCatalogModelResponse> addModelToCatalog(
            @PathVariable Long catalogId,
            @RequestBody @Valid MaintenanceCatalogModelRequest request
    ) {
        request.setMaintenanceCatalogId(catalogId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogModelService.create(request));
    }

    @PostMapping("/{catalogId}/models/batch")
    public ResponseEntity<List<MaintenanceCatalogModelResponse>> addModelsBatch(
            @PathVariable Long catalogId,
            @RequestBody @Valid List<MaintenanceCatalogModelRequest> requests
    ) {
        requests.forEach(r -> r.setMaintenanceCatalogId(catalogId));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogModelService.createBatch(requests));
    }

    @GetMapping("/{catalogId}/models")
    public ResponseEntity<List<MaintenanceCatalogModelResponse>> getModelsByCatalog(
            @PathVariable Long catalogId,
            @RequestParam(required = false) Long modelId,
            @RequestParam(defaultValue = "false") boolean includeParts
    ) {
        return ResponseEntity.ok(catalogModelService.findByCatalogId(catalogId, modelId, includeParts));
    }

    @PutMapping("/{catalogId}/models/{modelId}")
    public ResponseEntity<MaintenanceCatalogModelResponse> updateModelInCatalog(
            @PathVariable Long catalogId,
            @PathVariable Long modelId,
            @RequestBody MaintenanceCatalogModelRequest request
    ) {
        return ResponseEntity.ok(
                catalogModelService.updateByCatalogAndModel(catalogId, modelId, request)
        );
    }

    @DeleteMapping("/{catalogId}/models/{modelId}")
    public ResponseEntity<Void> deleteModelInCatalog(
            @PathVariable Long catalogId,
            @PathVariable Long modelId
    ) {
        catalogModelService.delete(catalogId, modelId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{catalogId}/models")
    public ResponseEntity<Void> deleteAllModelsInCatalog(
            @PathVariable Long catalogId
    ) {
        catalogModelService.deleteBatch(catalogId);
        return ResponseEntity.noContent().build();
    }

    // Catalog Model Part CRUD -----------------------------------

    @PostMapping("/{catalogId}/models/{modelId}/parts")
    public ResponseEntity<MaintenanceCatalogModelPartResponse> addPartToCatalogModel(
            @PathVariable Long catalogId,
            @PathVariable Long modelId,
            @RequestBody @Valid MaintenanceCatalogModelPartRequest request
    ) {
        request.setMaintenanceCatalogId(catalogId);
        request.setModelId(modelId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogModelPartService.create(request));
    }

    @PostMapping("/{catalogId}/models/{modelId}/parts/batch")
    public ResponseEntity<List<MaintenanceCatalogModelPartResponse>> addPartsBatch(
            @PathVariable Long catalogId,
            @PathVariable Long modelId,
            @RequestBody @Valid List<MaintenanceCatalogModelPartRequest> requests
    ) {
        requests.forEach(r -> {
            r.setMaintenanceCatalogId(catalogId);
            r.setModelId(modelId);
        });
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogModelPartService.createBatch(requests));
    }

    @GetMapping("/{catalogId}/models/{modelId}/parts")
    public ResponseEntity<List<MaintenanceCatalogModelPartResponse>> getPartsByCatalogModel(
            @PathVariable Long catalogId,
            @PathVariable Long modelId,
            @RequestParam(required = false) Long partId
    ) {
        return ResponseEntity.ok(
                catalogModelPartService.findByCatalogAndModel(catalogId, modelId, partId)
        );
    }

    @PutMapping("/{catalogId}/models/{modelId}/parts/{partId}")
    public ResponseEntity<MaintenanceCatalogModelPartResponse> updatePartInCatalogModel(
            @PathVariable Long catalogId,
            @PathVariable Long modelId,
            @PathVariable Long partId,
            @RequestBody MaintenanceCatalogModelPartRequest request
    ) {
        return ResponseEntity.ok(
                catalogModelPartService.update(catalogId, modelId, partId, request)
        );
    }

    @DeleteMapping("/{catalogId}/models/{modelId}/parts/{partId}")
    public ResponseEntity<Void> deletePartInCatalogModel(
            @PathVariable Long catalogId,
            @PathVariable Long modelId,
            @PathVariable Long partId
    ) {
        catalogModelPartService.delete(catalogId, modelId, partId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{catalogId}/models/{modelId}/parts")
    public ResponseEntity<Void> deleteAllPartsInCatalogModel(
            @PathVariable Long catalogId,
            @PathVariable Long modelId
    ) {
        catalogModelPartService.deleteBatch(catalogId, modelId);
        return ResponseEntity.noContent().build();
    }
}

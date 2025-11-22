package com.example.demo.controller;

import com.example.demo.model.dto.*;
import com.example.demo.model.modelEnum.MaintenanceCatalogCategory;
import com.example.demo.service.interfaces.IMaintenanceCatalogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-catalogs")
@RequiredArgsConstructor
@Tag(name = "Maintenance Catalog", description = "Endpoints for managing maintenance catalogs - Staff, Customer")
public class CatalogController {

    private final IMaintenanceCatalogService catalogService;

    @GetMapping
    @Operation(
        summary = "Get all maintenance catalogs",
        description = "Returns all maintenance catalogs, optionally filtered by category, VIN, or modelId. Requires authentication."
    )
    public ResponseEntity<List<CatalogResponse>> getAllCatalogs(
            @RequestParam(required = false) MaintenanceCatalogCategory category,
            @RequestParam(required = false) String vin,
            @RequestParam(required = false) Long modelId
    ) {
        return ResponseEntity.ok(catalogService.findAll(category, vin, modelId));
    }

    @GetMapping("/enum/category")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get maintenance catalog category enum schema",
        description = "Returns enum schema for maintenance catalog categories."
    )
    public ResponseEntity<EnumSchemaResponse> getCategoryEnumSchema() {
        return ResponseEntity.ok(catalogService.getCategoryEnumSchema());
    }

    @PostMapping
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Create a maintenance catalog",
        description = "Creates a new maintenance catalog. Requires staff permissions."
    )
    public ResponseEntity<CatalogResponse> createCatalog(
            @RequestBody @Valid CatalogRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogService.create(request));
    }

    @PutMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Update a maintenance catalog",
        description = "Updates an existing maintenance catalog by id. Requires staff permissions."
    )
    public ResponseEntity<CatalogResponse> updateCatalog(
            @PathVariable Long id,
            @RequestBody @Valid CatalogRequest request
    ) {
        return ResponseEntity.ok(catalogService.update(id, request));
    }

    @GetMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get maintenance catalog by id",
        description = "Returns a maintenance catalog by its id. Requires authentication."
    )
    public ResponseEntity<CatalogResponse> getCatalogById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(catalogService.findById(id));
    }

    @DeleteMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Delete a maintenance catalog",
        description = "Deletes a maintenance catalog by id. Requires staff permissions."
    )
    public ResponseEntity<Void> deleteCatalog(@PathVariable Long id) {
        catalogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

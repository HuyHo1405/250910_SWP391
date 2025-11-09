package com.example.demo.controller;

import com.example.demo.model.dto.*;
import com.example.demo.model.modelEnum.MaintenanceCatalogCategory;
import com.example.demo.service.interfaces.IMaintenanceCatalogService;
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
public class CatalogController {

    private final IMaintenanceCatalogService catalogService;

    @GetMapping
    public ResponseEntity<List<CatalogResponse>> getAllCatalogs(
            @RequestParam(required = false) MaintenanceCatalogCategory type,
            @RequestParam(required = false) String vin,
            @RequestParam(required = false) Long modelId
    ) {
        return ResponseEntity.ok(catalogService.findAll(type, vin, modelId));
    }

    @GetMapping("/enum/category")
    public ResponseEntity<EnumSchemaResponse> getCategoryEnumSchema() {
        return ResponseEntity.ok(catalogService.getCategoryEnumSchema());
    }

    @PostMapping
    public ResponseEntity<CatalogResponse> createCatalog(
            @RequestBody @Valid CatalogRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogResponse> updateCatalog(
            @PathVariable Long id,
            @RequestBody @Valid CatalogRequest request
    ) {
        return ResponseEntity.ok(catalogService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogResponse> getCatalogById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(catalogService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatalog(@PathVariable Long id) {
        catalogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

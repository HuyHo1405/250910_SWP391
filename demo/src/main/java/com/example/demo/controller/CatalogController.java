package com.example.demo.controller;

import com.example.demo.model.dto.*;
import com.example.demo.model.modelEnum.MaintenanceCatalogCategory;
import com.example.demo.service.interfaces.IMaintenanceCatalogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Page<CatalogResponse>> getAllCatalogsPaged(
            @RequestParam(required = false) MaintenanceCatalogCategory category,
            @RequestParam(required = false) String vin,
            @RequestParam(required = false) Long modelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(catalogService.findAllPaged(category, vin, modelId, pageable));
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

package com.example.demo.repo;

import com.example.demo.model.entity.MaintenanceCatalogModelPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceCatalogModelPartRepo extends JpaRepository<MaintenanceCatalogModelPart, Long> {

    Optional<MaintenanceCatalogModelPart> findByMaintenanceCatalogModelIdAndPartId(
            Long catalogModelId, Long partId);

    List<MaintenanceCatalogModelPart> findByMaintenanceCatalogModelId(
            Long catalogModelId);

    // Tìm tất cả catalog models sử dụng part này
    List<MaintenanceCatalogModelPart> findByPartId(Long partId);

    void deleteAllByMaintenanceCatalogModelId(Long catalogModelId);

    void deleteAllByMaintenanceCatalogModelIdIn(List<Long> toDeleteIds);
}

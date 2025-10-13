package com.example.demo.repo;
import com.example.demo.model.modelEnum.EntityStatus;
import com.example.demo.model.entity.MaintenanceService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceServiceRepo extends JpaRepository<MaintenanceService, Long> {

    // Find services by name (exact match)
    Optional<MaintenanceService> findByName(String name);

    // Find services by name containing keyword (case insensitive)
    List<MaintenanceService> findByNameContainingIgnoreCase(String keyword);

    // Find services by price range
    List<MaintenanceService> findByCurrentPriceBetween(Double minPrice, Double maxPrice);

    // Find services by price less than or equal
    List<MaintenanceService> findByCurrentPriceLessThanEqual(Double maxPrice);

    // Find services by price greater than or equal
    List<MaintenanceService> findByCurrentPriceGreaterThanEqual(Double minPrice);

    // Find services by description containing keyword (case insensitive)
    List<MaintenanceService> findByDescriptionContainingIgnoreCase(String keyword);

    List<MaintenanceService> findByStatus(EntityStatus status);
    

    // Find services ordered by price ascending
    List<MaintenanceService> findAllByOrderByCurrentPriceAsc();

    // Find services ordered by price descending
    List<MaintenanceService> findAllByOrderByCurrentPriceDesc();

    // Find services ordered by name
    List<MaintenanceService> findAllByOrderByNameAsc();

    // Custom query to find services with price in range and name containing keyword
    @Query("SELECT s FROM MaintenanceService s WHERE s.currentPrice BETWEEN :minPrice AND :maxPrice " +
            "AND UPPER(s.name) LIKE UPPER(CONCAT('%', :keyword, '%'))")
    List<MaintenanceService> findServicesByPriceRangeAndName(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("keyword") String keyword
    );

    // Custom query to find most expensive services (top N)
    @Query("SELECT s FROM MaintenanceService s ORDER BY s.currentPrice DESC")
    List<MaintenanceService> findTopServicesByPrice(@Param("limit") int limit);

    // Custom query to calculate average service price
    @Query("SELECT AVG(s.currentPrice) FROM MaintenanceService s")
    Double calculateAverageServicePrice();

    // Custom query to count services by price range
    @Query("SELECT COUNT(s) FROM MaintenanceService s WHERE s.currentPrice BETWEEN :minPrice AND :maxPrice")
    Long countServicesByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // Custom query for service statistics
    @Query("SELECT new map(COUNT(s) as totalServices, AVG(s.currentPrice) as avgPrice, " +
            "MIN(s.currentPrice) as minPrice, MAX(s.currentPrice) as maxPrice) FROM MaintenanceService s")
    List<Object> getServiceStatistics();

    Optional<MaintenanceService> findByIdAndStatus(Long id, EntityStatus entityStatus);
}

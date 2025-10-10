package com.example.demo.repo;

import com.example.demo.model.entity.EntityStatus;
import com.example.demo.model.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepo extends JpaRepository<Service, Long> {

    // Find services by name (exact match)
    Optional<Service> findByName(String name);

    // Find services by name containing keyword (case insensitive)
    List<Service> findByNameContainingIgnoreCase(String keyword);

    // Find services by price range
    List<Service> findByCurrentPriceBetween(Double minPrice, Double maxPrice);

    // Find services by price less than or equal
    List<Service> findByCurrentPriceLessThanEqual(Double maxPrice);

    // Find services by price greater than or equal
    List<Service> findByCurrentPriceGreaterThanEqual(Double minPrice);

    // Find services by description containing keyword (case insensitive)
    List<Service> findByDescriptionContainingIgnoreCase(String keyword);

    List<Service> findByStatus(EntityStatus status);
    

    // Find services ordered by price ascending
    List<Service> findAllByOrderByCurrentPriceAsc();

    // Find services ordered by price descending
    List<Service> findAllByOrderByCurrentPriceDesc();

    // Find services ordered by name
    List<Service> findAllByOrderByNameAsc();

    // Custom query to find services with price in range and name containing keyword
    @Query("SELECT s FROM Service s WHERE s.currentPrice BETWEEN :minPrice AND :maxPrice " +
            "AND UPPER(s.name) LIKE UPPER(CONCAT('%', :keyword, '%'))")
    List<Service> findServicesByPriceRangeAndName(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("keyword") String keyword
    );

    // Custom query to find most expensive services (top N)
    @Query("SELECT s FROM Service s ORDER BY s.currentPrice DESC")
    List<Service> findTopServicesByPrice(@Param("limit") int limit);

    // Custom query to calculate average service price
    @Query("SELECT AVG(s.currentPrice) FROM Service s")
    Double calculateAverageServicePrice();

    // Custom query to count services by price range
    @Query("SELECT COUNT(s) FROM Service s WHERE s.currentPrice BETWEEN :minPrice AND :maxPrice")
    Long countServicesByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // Custom query for service statistics
    @Query("SELECT new map(COUNT(s) as totalServices, AVG(s.currentPrice) as avgPrice, " +
            "MIN(s.currentPrice) as minPrice, MAX(s.currentPrice) as maxPrice) FROM Service s")
    List<Object> getServiceStatistics();
}

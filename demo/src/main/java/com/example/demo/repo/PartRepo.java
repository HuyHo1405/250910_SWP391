package com.example.demo.repo;

import com.example.demo.model.entity.Part;
import com.example.demo.model.modelEnum.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PartRepo extends JpaRepository<Part, Long> {

    Optional<Part> findByPartNumber(String partNumber);

    boolean existsByPartNumber(String partNumber);

    List<Part> findByManufacturer(String manufacturer);

    List<Part> findByStatus(EntityStatus status);

    List<Part> findByQuantityLessThanEqual(BigDecimal threshold);

    List<Part> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT p FROM Part p WHERE " +
            "(:manufacturer IS NULL OR LOWER(p.manufacturer) = LOWER(:manufacturer)) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:search IS NULL OR LOWER(p.name) LIKE %:search%)")
    List<Part> findFilteredAll(@Param("manufacturer") String manufacturer,
                               @Param("status") EntityStatus status,
                               @Param("search") String search);


}

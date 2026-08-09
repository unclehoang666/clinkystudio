package com.example.backend.repository.catalog;

import com.example.backend.entity.catalog.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCodeIgnoreCase(String code);

    List<Brand> findByStatusTrue();

    @Query("SELECT b FROM Brand b WHERE " +
           "(:q IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
           "(:status IS NULL OR b.status = :status)")
    Page<Brand> search(@Param("q") String q, @Param("status") Boolean status, Pageable pageable);
}
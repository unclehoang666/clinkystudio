package com.example.backend.repository.product;

import com.example.backend.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCategory_IdAndStatusTrue(Integer categoryId);

    boolean existsByBrand_IdAndStatusTrue(Integer brandId);

    @Query("SELECT p FROM Product p WHERE " +
           "(:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:brandId IS NULL OR p.brand.id = :brandId) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Product> search(@Param("q") String q,
                          @Param("categoryId") Integer categoryId,
                          @Param("brandId") Integer brandId,
                          @Param("status") Boolean status,
                          Pageable pageable);
}
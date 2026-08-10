package com.example.backend.repository.product;

import com.example.backend.entity.product.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {

    boolean existsBySkuIgnoreCase(String sku);

    List<ProductVariant> findByProduct_Id(Integer productId);

    boolean existsByProduct_IdAndStatusTrue(Integer productId);
}
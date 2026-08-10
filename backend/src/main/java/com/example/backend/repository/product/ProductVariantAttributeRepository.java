package com.example.backend.repository.product;

import com.example.backend.entity.product.ProductVariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantAttributeRepository extends JpaRepository<ProductVariantAttribute, Integer> {

    List<ProductVariantAttribute> findByVariant_Id(Integer variantId);

    void deleteByVariant_Id(Integer variantId);
}
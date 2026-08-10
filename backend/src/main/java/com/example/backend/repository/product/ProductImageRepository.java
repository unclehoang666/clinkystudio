package com.example.backend.repository.product;

import com.example.backend.entity.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    List<ProductImage> findByProduct_IdOrderBySortOrderAsc(Integer productId);

    void deleteByProduct_Id(Integer productId);
}
package com.example.backend.repository.promotion;

import com.example.backend.entity.promotion.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Integer> {

    List<PromotionProduct> findByPromotion_Id(Integer promotionId);

    // Tim cac khuyen mai dang hoat dong ap dung cho 1 variant cu the (ca truong hop gan truc tiep variant hoac gan ca product)
    @org.springframework.data.jpa.repository.Query(
        "SELECT pp FROM PromotionProduct pp WHERE " +
        "(pp.variant.id = :variantId OR pp.product.id = :productId) AND " +
        "pp.promotion.status = true AND :now BETWEEN pp.promotion.startDate AND pp.promotion.endDate"
    )
    List<PromotionProduct> findActiveByVariantOrProduct(
        @org.springframework.data.repository.query.Param("variantId") Integer variantId,
        @org.springframework.data.repository.query.Param("productId") Integer productId,
        @org.springframework.data.repository.query.Param("now") LocalDateTime now
    );
}
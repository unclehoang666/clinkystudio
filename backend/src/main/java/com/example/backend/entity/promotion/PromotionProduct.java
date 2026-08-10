package com.example.backend.entity.promotion;

import com.example.backend.entity.product.Product;
import com.example.backend.entity.product.ProductVariant;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "promotion_products")
@Data
public class PromotionProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;       // ap dung cho ca san pham (moi variant)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant; // hoac chi ap dung 1 variant cu the
}
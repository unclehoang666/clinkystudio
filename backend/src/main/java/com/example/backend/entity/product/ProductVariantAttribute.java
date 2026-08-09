package com.example.backend.entity.product;

import com.example.backend.entity.catalog.AttributeValue;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_variant_attributes")
@Data
public class ProductVariantAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_value_id", nullable = false)
    private AttributeValue attributeValue;
}
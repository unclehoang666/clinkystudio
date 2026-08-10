package com.example.backend.dto.product;

import lombok.Data;
import java.util.List;

@Data
public class ProductRequest {
    private String code;
    private String name;
    private String description;
    private String metaTitle;
    private String metaDescription;
    private Integer brandId;
    private Integer categoryId;
    private Boolean status;
    private Boolean isGiveaway;
    private List<String> imageUrls;
    private List<VariantRequest> variants;
}
package com.example.backend.dto.promotion;

import lombok.Data;

@Data
public class PromotionProductRequest {
    private Integer promotionId;
    private Integer productId;    // dien 1 trong 2: productId (ap dung ca san pham) hoac variantId (chi 1 variant)
    private Integer variantId;
}
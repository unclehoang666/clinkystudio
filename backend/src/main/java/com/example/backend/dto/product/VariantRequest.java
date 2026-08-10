package com.example.backend.dto.product;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class VariantRequest {
    private Integer id;              // null neu la variant moi, co gia tri neu dang sua
    private String sku;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;
    private Boolean status;
    private List<VariantAttributeItem> attributes;   // vd: [{attributeValueId: 3}, {attributeValueId: 7}]
}
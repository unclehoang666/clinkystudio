package com.example.backend.dto.catalog;

import lombok.Data;

@Data
public class CategoryAttributeRequest {
    private Integer categoryId;
    private Integer attributeId;
}
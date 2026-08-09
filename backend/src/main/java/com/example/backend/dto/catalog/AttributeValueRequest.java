package com.example.backend.dto.catalog;

import lombok.Data;

@Data
public class AttributeValueRequest {
    private Integer attributeId;
    private String value;
}
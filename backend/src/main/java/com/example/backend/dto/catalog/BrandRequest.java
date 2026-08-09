package com.example.backend.dto.catalog;

import lombok.Data;

@Data
public class BrandRequest {
    private String code;
    private String name;
    private String description;
    private Boolean status;
}
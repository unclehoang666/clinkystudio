package com.example.backend.dto.catalog;

import lombok.Data;

@Data
public class CategoryRequest {
    private String code;
    private String name;
    private String description;
    private Integer parentId;
    private Boolean status;
}
package com.example.backend.dto.staff;

import lombok.Data;

@Data
public class PositionRequest {
    private String code;
    private String name;
    private String description;
}
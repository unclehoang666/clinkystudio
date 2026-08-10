package com.example.backend.dto.review;

import lombok.Data;

@Data
public class ReviewRequest {
    private Integer orderItemId;
    private Integer rating;
    private String content;
    private String category;
    private String images;   // chuoi JSON list url, optional
}
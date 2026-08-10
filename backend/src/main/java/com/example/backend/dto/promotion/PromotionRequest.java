package com.example.backend.dto.promotion;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromotionRequest {
    private String code;
    private String name;
    private String type;             // PERCENT / FIXED
    private BigDecimal value;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean allowStacking;
    private Boolean status;
}
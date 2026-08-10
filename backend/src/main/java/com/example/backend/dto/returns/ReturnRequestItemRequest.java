package com.example.backend.dto.returns;

import lombok.Data;

@Data
public class ReturnRequestItemRequest {
    private Integer orderItemId;
    private Integer quantity;
    private String itemCondition;
}
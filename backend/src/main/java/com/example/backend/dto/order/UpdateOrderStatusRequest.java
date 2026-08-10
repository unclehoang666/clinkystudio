package com.example.backend.dto.order;

import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private String statusCode;   // vd: CONFIRMED, SHIPPING, COMPLETED, CANCELLED
    private String note;
}
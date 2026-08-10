package com.example.backend.dto.warehouse;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PurchaseOrderItemRequest {
    private Integer variantId;
    private Integer quantity;
    private BigDecimal importPrice;
}
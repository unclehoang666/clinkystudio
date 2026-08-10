package com.example.backend.dto.warehouse;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseOrderRequest {
    private Integer supplierId;
    private String note;
    private List<PurchaseOrderItemRequest> items;
}
package com.example.backend.dto.cart;

import lombok.Data;

@Data
public class CartItemRequest {
    private Integer variantId;
    private Integer quantity;
}
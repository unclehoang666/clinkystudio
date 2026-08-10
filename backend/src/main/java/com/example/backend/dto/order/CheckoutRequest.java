package com.example.backend.dto.order;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String shippingWard;
    private String shippingDistrict;
    private String shippingProvince;
    private String note;
    private String couponCode;      // optional
    private String deliveryMethod;  // HOME_DELIVERY / STORE_PICKUP
}
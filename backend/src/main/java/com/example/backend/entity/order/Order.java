package com.example.backend.entity.order;

import com.example.backend.entity.User;
import com.example.backend.entity.promotion.Coupon;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 50)
    private String code;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "shipping_fee", precision = 18, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 18, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "receiver_name", length = 150)
    private String receiverName;

    @Column(name = "receiver_phone", length = 15)
    private String receiverPhone;

    @Column(name = "shipping_address", length = 255)
    private String shippingAddress;

    @Column(name = "shipping_ward", length = 100)
    private String shippingWard;

    @Column(name = "shipping_district", length = 100)
    private String shippingDistrict;

    @Column(name = "shipping_province", length = 100)
    private String shippingProvince;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @Column(name = "order_type")
    private Integer orderType = 0;

    @Column(name = "delivery_method", length = 50)
    private String deliveryMethod = "HOME_DELIVERY";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private OrderStatus status;
}
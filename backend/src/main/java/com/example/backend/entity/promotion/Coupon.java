package com.example.backend.entity.promotion;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "discount_type", nullable = false, length = 50)
    private String discountType;          // PERCENT / FIXED

    @Column(name = "discount_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "max_discount_value", precision = 18, scale = 2)
    private BigDecimal maxDiscountValue;

    @Column(name = "min_order_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal minOrderValue;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "used_count", nullable = false)
    private Integer usedCount = 0;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "allow_stacking")
    private Boolean allowStacking = false;

    @Column(nullable = false)
    private Boolean status = true;
}
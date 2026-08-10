package com.example.backend.entity.order;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_statuses")
@Data
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String code;      // PENDING, CONFIRMED, SHIPPING, COMPLETED, CANCELLED

    @Column(nullable = false, length = 100)
    private String name;
}
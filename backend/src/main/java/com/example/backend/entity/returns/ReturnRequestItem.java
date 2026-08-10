package com.example.backend.entity.returns;

import com.example.backend.entity.order.OrderItem;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "return_request_items")
@Data
public class ReturnRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_request_id", nullable = false)
    private ReturnRequest returnRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "item_condition", columnDefinition = "NVARCHAR(MAX)")
    private String itemCondition;
}
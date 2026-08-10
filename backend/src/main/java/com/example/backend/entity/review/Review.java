package com.example.backend.entity.review;

import com.example.backend.entity.User;
import com.example.backend.entity.order.OrderItem;
import com.example.backend.entity.product.Product;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    private Integer rating;   // 1-5 sao

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(length = 100)
    private String category;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String images;   // luu list url dang chuoi JSON, don gian cho quy mo hien tai

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_edited")
    private Boolean isEdited = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replied_by_employee_id")
    private User repliedBy;

    @Column(name = "reply_content", columnDefinition = "NVARCHAR(MAX)")
    private String replyContent;

    @Column(name = "replied_at")
    private LocalDateTime repliedAt;
}
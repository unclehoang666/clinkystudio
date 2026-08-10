package com.example.backend.entity.returns;

import com.example.backend.entity.User;
import com.example.backend.entity.order.Order;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "return_requests")
@Data
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private User processedBy;

    @Column(name = "request_type", nullable = false, length = 50)
    private String requestType;     // EXCHANGE / RETURN

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String reason;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @Column(name = "refund_amount", precision = 18, scale = 2)
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 50)
    private String status = "PENDING";   // PENDING / APPROVED / REJECTED / COMPLETED

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
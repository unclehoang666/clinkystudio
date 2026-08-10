package com.example.backend.repository.order;

import com.example.backend.entity.order.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Integer> {
    List<OrderStatusHistory> findByOrder_IdOrderByCreatedAtAsc(Integer orderId);
}
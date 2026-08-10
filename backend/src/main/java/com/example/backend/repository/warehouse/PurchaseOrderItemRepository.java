package com.example.backend.repository.warehouse;

import com.example.backend.entity.warehouse.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Integer> {
    List<PurchaseOrderItem> findByPurchaseOrder_Id(Integer purchaseOrderId);
}
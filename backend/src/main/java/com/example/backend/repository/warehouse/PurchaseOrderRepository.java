package com.example.backend.repository.warehouse;

import com.example.backend.entity.warehouse.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {

    boolean existsByCodeIgnoreCase(String code);

    @Query("SELECT p FROM PurchaseOrder p WHERE " +
           "(:supplierId IS NULL OR p.supplier.id = :supplierId) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<PurchaseOrder> search(@Param("supplierId") Integer supplierId,
                                @Param("status") String status,
                                Pageable pageable);
}
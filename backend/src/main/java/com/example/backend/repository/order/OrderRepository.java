package com.example.backend.repository.order;

import com.example.backend.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUser_IdOrderByCreatedAtDesc(Integer userId);

    @Query("SELECT o FROM Order o WHERE " +
           "(:userId IS NULL OR o.user.id = :userId) AND " +
           "(:statusId IS NULL OR o.status.id = :statusId) AND " +
           "(:code IS NULL OR LOWER(o.code) LIKE LOWER(CONCAT('%', :code, '%')))")
    Page<Order> search(@Param("userId") Integer userId,
                        @Param("statusId") Integer statusId,
                        @Param("code") String code,
                        Pageable pageable);
}
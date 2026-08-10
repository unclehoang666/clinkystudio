package com.example.backend.repository.promotion;

import com.example.backend.entity.promotion.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    @Query("SELECT p FROM Promotion p WHERE (:status IS NULL OR p.status = :status)")
    Page<Promotion> search(@Param("status") Boolean status, Pageable pageable);
}
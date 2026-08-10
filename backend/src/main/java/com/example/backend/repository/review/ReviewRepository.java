package com.example.backend.repository.review;

import com.example.backend.entity.review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProduct_Id(Integer productId, Pageable pageable);

    List<Review> findByUser_Id(Integer userId);

    boolean existsByOrderItem_Id(Integer orderItemId);

    Optional<Review> findByOrderItem_Id(Integer orderItemId);
}
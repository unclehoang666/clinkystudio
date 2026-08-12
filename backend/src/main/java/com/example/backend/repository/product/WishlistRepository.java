package com.example.backend.repository.product;

import com.example.backend.entity.product.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUser_Id(Integer userId);

    Optional<Wishlist> findByUser_IdAndProduct_Id(Integer userId, Integer productId);

    boolean existsByUser_IdAndProduct_Id(Integer userId, Integer productId);

    void deleteByUser_IdAndProduct_Id(Integer userId, Integer productId);
}
package com.example.backend.repository.cart;

import com.example.backend.entity.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser_Id(Integer userId);
}
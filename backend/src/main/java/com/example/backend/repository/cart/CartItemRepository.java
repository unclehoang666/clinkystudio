package com.example.backend.repository.cart;

import com.example.backend.entity.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByCart_Id(Integer cartId);

    Optional<CartItem> findByCart_IdAndVariant_Id(Integer cartId, Integer variantId);

    void deleteByCart_IdAndVariant_Id(Integer cartId, Integer variantId);

    void deleteByCart_Id(Integer cartId);
}
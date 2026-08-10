package com.example.backend.controller.cart;

import com.example.backend.dto.cart.CartItemRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.cart.CartItem;
import com.example.backend.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // JwtAuthFilter da gan User lam principal, nen lay thang tu Authentication, khong can client truyen userId
    private User currentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @GetMapping
    public List<CartItem> getCart(Authentication authentication) {
        return cartService.getItems(currentUser(authentication));
    }

    @GetMapping("/total")
    public Map<String, BigDecimal> getTotal(Authentication authentication) {
        return Map.of("total", cartService.getTotal(currentUser(authentication)));
    }

    @PostMapping("/items")
    public ResponseEntity<CartItem> addItem(Authentication authentication, @RequestBody CartItemRequest req) {
        return ResponseEntity.ok(cartService.addItem(currentUser(authentication), req));
    }

    @PutMapping("/items/{variantId}")
    public CartItem updateQuantity(Authentication authentication, @PathVariable Integer variantId,
                                    @RequestBody Map<String, Integer> body) {
        return cartService.updateQuantity(currentUser(authentication), variantId, body.get("quantity"));
    }

    @DeleteMapping("/items/{variantId}")
    public ResponseEntity<?> removeItem(Authentication authentication, @PathVariable Integer variantId) {
        cartService.removeItem(currentUser(authentication), variantId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(Authentication authentication) {
        cartService.clearCart(currentUser(authentication));
        return ResponseEntity.ok().build();
    }
}
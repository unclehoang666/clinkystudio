package com.example.backend.controller.product;

import com.example.backend.entity.User;
import com.example.backend.entity.product.Wishlist;
import com.example.backend.service.product.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    private User currentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @GetMapping
    public List<Wishlist> getMyWishlist(Authentication authentication) {
        return wishlistService.getMyWishlist(currentUser(authentication));
    }

    @GetMapping("/check/{productId}")
    public Map<String, Boolean> check(Authentication authentication, @PathVariable Integer productId) {
        return Map.of("wishlisted", wishlistService.isWishlisted(currentUser(authentication), productId));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Wishlist> add(Authentication authentication, @PathVariable Integer productId) {
        return ResponseEntity.ok(wishlistService.add(currentUser(authentication), productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> remove(Authentication authentication, @PathVariable Integer productId) {
        wishlistService.remove(currentUser(authentication), productId);
        return ResponseEntity.ok().build();
    }
}
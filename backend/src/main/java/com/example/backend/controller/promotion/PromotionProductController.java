package com.example.backend.controller.promotion;

import com.example.backend.dto.promotion.PromotionProductRequest;
import com.example.backend.entity.promotion.PromotionProduct;
import com.example.backend.service.promotion.PromotionProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotion-products")
@RequiredArgsConstructor
public class PromotionProductController {

    private final PromotionProductService promotionProductService;

    @GetMapping
    public List<PromotionProduct> getByPromotion(@RequestParam Integer promotionId) {
        return promotionProductService.getByPromotion(promotionId);
    }

    @PostMapping
    public ResponseEntity<PromotionProduct> assign(@RequestBody PromotionProductRequest req) {
        return ResponseEntity.ok(promotionProductService.assign(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> unassign(@PathVariable Integer id) {
        promotionProductService.unassign(id);
        return ResponseEntity.ok().build();
    }
}
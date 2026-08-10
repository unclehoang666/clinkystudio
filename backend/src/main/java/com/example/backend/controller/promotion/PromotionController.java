package com.example.backend.controller.promotion;

import com.example.backend.dto.promotion.PromotionRequest;
import com.example.backend.entity.promotion.Promotion;
import com.example.backend.service.promotion.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public Page<Promotion> list(@RequestParam(required = false) Boolean status,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return promotionService.search(status, page, size);
    }

    @GetMapping("/{id}")
    public Promotion getById(@PathVariable Integer id) {
        return promotionService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Promotion> create(@RequestBody PromotionRequest req) {
        return ResponseEntity.ok(promotionService.create(req));
    }

    @PutMapping("/{id}")
    public Promotion update(@PathVariable Integer id, @RequestBody PromotionRequest req) {
        return promotionService.update(id, req);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id) {
        promotionService.toggleStatus(id);
        return ResponseEntity.ok().build();
    }
}
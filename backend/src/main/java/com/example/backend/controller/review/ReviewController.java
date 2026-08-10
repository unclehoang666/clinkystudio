package com.example.backend.controller.review;

import com.example.backend.dto.review.ReviewReplyRequest;
import com.example.backend.dto.review.ReviewRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.review.Review;
import com.example.backend.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    private User currentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @GetMapping("/product/{productId}")
    public Page<Review> getByProduct(@PathVariable Integer productId,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return reviewService.getByProduct(productId, page, size);
    }

    @GetMapping("/my-reviews")
    public List<Review> myReviews(Authentication authentication) {
        return reviewService.getMyReviews(currentUser(authentication));
    }

    @PostMapping
    public ResponseEntity<Review> create(Authentication authentication, @RequestBody ReviewRequest req) {
        return ResponseEntity.ok(reviewService.create(currentUser(authentication), req));
    }

    @PutMapping("/{id}")
    public Review update(Authentication authentication, @PathVariable Long id, @RequestBody ReviewRequest req) {
        return reviewService.update(currentUser(authentication), id, req);
    }

    @PostMapping("/{id}/reply")
    public Review reply(Authentication authentication, @PathVariable Long id, @RequestBody ReviewReplyRequest req) {
        return reviewService.reply(currentUser(authentication), id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Authentication authentication, @PathVariable Long id) {
        reviewService.delete(currentUser(authentication), id);
        return ResponseEntity.ok().build();
    }
}
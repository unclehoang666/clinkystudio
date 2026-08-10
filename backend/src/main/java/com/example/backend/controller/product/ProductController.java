package com.example.backend.controller.product;

import com.example.backend.dto.product.ProductRequest;
import com.example.backend.entity.product.Product;
import com.example.backend.entity.product.ProductImage;
import com.example.backend.entity.product.ProductVariant;
import com.example.backend.entity.product.ProductVariantAttribute;
import com.example.backend.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<Product> list(@RequestParam(required = false) String q,
                               @RequestParam(required = false) Integer categoryId,
                               @RequestParam(required = false) Integer brandId,
                               @RequestParam(required = false) Boolean status,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        return productService.search(q, categoryId, brandId, status, page, size);
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Integer id) {
        return productService.getById(id);
    }

    @GetMapping("/{id}/images")
    public List<ProductImage> getImages(@PathVariable Integer id) {
        return productService.getImages(id);
    }

    @GetMapping("/{id}/variants")
    public List<ProductVariant> getVariants(@PathVariable Integer id) {
        return productService.getVariants(id);
    }

    @GetMapping("/variants/{variantId}/attributes")
    public List<ProductVariantAttribute> getVariantAttributes(@PathVariable Integer variantId) {
        return productService.getVariantAttributes(variantId);
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductRequest req) {
        return ResponseEntity.ok(productService.create(req));
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Integer id, @RequestBody ProductRequest req) {
        return productService.update(id, req);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id) {
        productService.toggleStatus(id);
        return ResponseEntity.ok().build();
    }
}
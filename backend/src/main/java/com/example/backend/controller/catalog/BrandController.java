package com.example.backend.controller.catalog;

import com.example.backend.dto.catalog.BrandRequest;
import com.example.backend.entity.catalog.Brand;
import com.example.backend.service.catalog.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public Page<Brand> list(@RequestParam(required = false) String q,
                             @RequestParam(required = false) Boolean status,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        return brandService.search(q, status, page, size);
    }

    @GetMapping("/all-active")
    public ResponseEntity<List<Brand>> getAllActive() {
        return ResponseEntity.ok(brandService.getAllActive());
    }

    @GetMapping("/{id}")
    public Brand getById(@PathVariable Integer id) {
        return brandService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Brand> create(@RequestBody BrandRequest req) {
        return ResponseEntity.ok(brandService.create(req));
    }

    @PutMapping("/{id}")
    public Brand update(@PathVariable Integer id, @RequestBody BrandRequest req) {
        return brandService.update(id, req);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id) {
        brandService.toggleStatus(id);
        return ResponseEntity.ok().build();
    }
}
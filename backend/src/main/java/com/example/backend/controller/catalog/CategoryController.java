package com.example.backend.controller.catalog;

import com.example.backend.dto.catalog.CategoryRequest;
import com.example.backend.entity.catalog.Category;
import com.example.backend.service.catalog.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Page<Category> list(@RequestParam(required = false) String q,
                                @RequestParam(required = false) Boolean status,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return categoryService.search(q, status, page, size);
    }

    @GetMapping("/all-active")
    public ResponseEntity<List<Category>> getAllActive() {
        return ResponseEntity.ok(categoryService.getAllActive());
    }

    @GetMapping("/roots")
    public ResponseEntity<List<Category>> getRoots() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<Category>> getChildren(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.getChildren(id));
    }

    @GetMapping("/{id}")
    public Category getById(@PathVariable Integer id) {
        return categoryService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody CategoryRequest req) {
        return ResponseEntity.ok(categoryService.create(req));
    }

    @PutMapping("/{id}")
    public Category update(@PathVariable Integer id, @RequestBody CategoryRequest req) {
        return categoryService.update(id, req);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id) {
        categoryService.toggleStatus(id);
        return ResponseEntity.ok().build();
    }
}
package com.example.backend.controller.catalog;

import com.example.backend.dto.catalog.CategoryAttributeRequest;
import com.example.backend.entity.catalog.CategoryAttribute;
import com.example.backend.service.catalog.CategoryAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category-attributes")
@RequiredArgsConstructor
public class CategoryAttributeController {

    private final CategoryAttributeService categoryAttributeService;

    @GetMapping
    public List<CategoryAttribute> getByCategory(@RequestParam Integer categoryId) {
        return categoryAttributeService.getByCategory(categoryId);
    }

    @PostMapping
    public ResponseEntity<CategoryAttribute> assign(@RequestBody CategoryAttributeRequest req) {
        return ResponseEntity.ok(categoryAttributeService.assign(req));
    }

    @DeleteMapping
    public ResponseEntity<?> unassign(@RequestParam Integer categoryId, @RequestParam Integer attributeId) {
        categoryAttributeService.unassign(categoryId, attributeId);
        return ResponseEntity.ok().build();
    }
}
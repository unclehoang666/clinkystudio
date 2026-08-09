package com.example.backend.controller.catalog;

import com.example.backend.dto.catalog.AttributeRequest;
import com.example.backend.entity.catalog.Attribute;
import com.example.backend.service.catalog.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    @GetMapping
    public List<Attribute> list() {
        return attributeService.getAll();
    }

    @GetMapping("/{id}")
    public Attribute getById(@PathVariable Integer id) {
        return attributeService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Attribute> create(@RequestBody AttributeRequest req) {
        return ResponseEntity.ok(attributeService.create(req));
    }

    @PutMapping("/{id}")
    public Attribute update(@PathVariable Integer id, @RequestBody AttributeRequest req) {
        return attributeService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        attributeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
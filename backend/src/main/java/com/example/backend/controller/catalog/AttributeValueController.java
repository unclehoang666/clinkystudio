package com.example.backend.controller.catalog;

import com.example.backend.dto.catalog.AttributeValueRequest;
import com.example.backend.entity.catalog.AttributeValue;
import com.example.backend.service.catalog.AttributeValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attribute-values")
@RequiredArgsConstructor
public class AttributeValueController {

    private final AttributeValueService attributeValueService;

    @GetMapping
    public List<AttributeValue> getByAttribute(@RequestParam Integer attributeId) {
        return attributeValueService.getByAttribute(attributeId);
    }

    @GetMapping("/{id}")
    public AttributeValue getById(@PathVariable Integer id) {
        return attributeValueService.getById(id);
    }

    @PostMapping
    public ResponseEntity<AttributeValue> create(@RequestBody AttributeValueRequest req) {
        return ResponseEntity.ok(attributeValueService.create(req));
    }

    @PutMapping("/{id}")
    public AttributeValue update(@PathVariable Integer id, @RequestBody AttributeValueRequest req) {
        return attributeValueService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        attributeValueService.delete(id);
        return ResponseEntity.ok().build();
    }
}
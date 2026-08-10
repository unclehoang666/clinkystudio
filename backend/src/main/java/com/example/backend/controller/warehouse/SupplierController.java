package com.example.backend.controller.warehouse;

import com.example.backend.dto.warehouse.SupplierRequest;
import com.example.backend.entity.warehouse.Supplier;
import com.example.backend.service.warehouse.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public Page<Supplier> list(@RequestParam(required = false) String q,
                                @RequestParam(required = false) Boolean status,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return supplierService.search(q, status, page, size);
    }

    @GetMapping("/{id}")
    public Supplier getById(@PathVariable Integer id) {
        return supplierService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Supplier> create(@RequestBody SupplierRequest req) {
        return ResponseEntity.ok(supplierService.create(req));
    }

    @PutMapping("/{id}")
    public Supplier update(@PathVariable Integer id, @RequestBody SupplierRequest req) {
        return supplierService.update(id, req);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id) {
        supplierService.toggleStatus(id);
        return ResponseEntity.ok().build();
    }
}
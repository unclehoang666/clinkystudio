package com.example.backend.controller.warehouse;

import com.example.backend.dto.warehouse.PurchaseOrderRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.warehouse.PurchaseOrder;
import com.example.backend.entity.warehouse.PurchaseOrderItem;
import com.example.backend.service.warehouse.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    private User currentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @GetMapping
    public Page<PurchaseOrder> list(@RequestParam(required = false) Integer supplierId,
                                     @RequestParam(required = false) String status,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return purchaseOrderService.search(supplierId, status, page, size);
    }

    @GetMapping("/{id}")
    public PurchaseOrder getById(@PathVariable Integer id) {
        return purchaseOrderService.getById(id);
    }

    @GetMapping("/{id}/items")
    public List<PurchaseOrderItem> getItems(@PathVariable Integer id) {
        return purchaseOrderService.getItems(id);
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> create(Authentication authentication, @RequestBody PurchaseOrderRequest req) {
        return ResponseEntity.ok(purchaseOrderService.create(currentUser(authentication), req));
    }

    @PatchMapping("/{id}/confirm")
    public PurchaseOrder confirm(@PathVariable Integer id) {
        return purchaseOrderService.confirm(id);
    }

    @PatchMapping("/{id}/cancel")
    public PurchaseOrder cancel(@PathVariable Integer id) {
        return purchaseOrderService.cancel(id);
    }
}
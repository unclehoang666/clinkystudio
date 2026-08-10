package com.example.backend.controller.order;

import com.example.backend.dto.order.CheckoutRequest;
import com.example.backend.dto.order.UpdateOrderStatusRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.order.Order;
import com.example.backend.entity.order.OrderItem;
import com.example.backend.entity.order.OrderStatusHistory;
import com.example.backend.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private User currentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    // Danh cho ADMIN - xem toan bo don hang, co filter
    @GetMapping
    public Page<Order> list(@RequestParam(required = false) Integer userId,
                             @RequestParam(required = false) Integer statusId,
                             @RequestParam(required = false) String code,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size) {
        return orderService.search(userId, statusId, code, page, size);
    }

    // Danh cho khach hang - xem don hang cua chinh minh
    @GetMapping("/my-orders")
    public List<Order> myOrders(Authentication authentication) {
        return orderService.getMyOrders(currentUser(authentication));
    }

    @GetMapping("/{id}")
    public Order getById(@PathVariable Integer id) {
        return orderService.getById(id);
    }

    @GetMapping("/{id}/items")
    public List<OrderItem> getItems(@PathVariable Integer id) {
        return orderService.getItems(id);
    }

    @GetMapping("/{id}/history")
    public List<OrderStatusHistory> getHistory(@PathVariable Integer id) {
        return orderService.getHistory(id);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(Authentication authentication, @RequestBody CheckoutRequest req) {
        return ResponseEntity.ok(orderService.checkout(currentUser(authentication), req));
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable Integer id, @RequestBody UpdateOrderStatusRequest req) {
        return orderService.updateStatus(id, req);
    }
}
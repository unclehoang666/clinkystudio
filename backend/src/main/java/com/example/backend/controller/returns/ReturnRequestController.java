package com.example.backend.controller.returns;

import com.example.backend.dto.returns.ReturnRequestCreateRequest;
import com.example.backend.dto.returns.ReturnRequestProcessRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.returns.ReturnRequest;
import com.example.backend.entity.returns.ReturnRequestItem;
import com.example.backend.service.returns.ReturnRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/return-requests")
@RequiredArgsConstructor
public class ReturnRequestController {

    private final ReturnRequestService returnRequestService;

    private User currentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    // Danh cho ADMIN - xem toan bo yeu cau
    @GetMapping
    public Page<ReturnRequest> list(@RequestParam(required = false) String status,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return returnRequestService.search(status, page, size);
    }

    // Danh cho khach hang - xem yeu cau cua chinh minh
    @GetMapping("/my-requests")
    public List<ReturnRequest> myRequests(Authentication authentication) {
        return returnRequestService.getMyReturnRequests(currentUser(authentication));
    }

    @GetMapping("/{id}")
    public ReturnRequest getById(@PathVariable Integer id) {
        return returnRequestService.getById(id);
    }

    @GetMapping("/{id}/items")
    public List<ReturnRequestItem> getItems(@PathVariable Integer id) {
        return returnRequestService.getItems(id);
    }

    @PostMapping
    public ResponseEntity<ReturnRequest> create(Authentication authentication, @RequestBody ReturnRequestCreateRequest req) {
        return ResponseEntity.ok(returnRequestService.create(currentUser(authentication), req));
    }

    @PatchMapping("/{id}/process")
    public ReturnRequest process(Authentication authentication, @PathVariable Integer id,
                                  @RequestBody ReturnRequestProcessRequest req) {
        return returnRequestService.process(currentUser(authentication), id, req);
    }
}
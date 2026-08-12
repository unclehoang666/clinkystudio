package com.example.backend.controller;

import com.example.backend.dto.ChangePasswordRequest;
import com.example.backend.dto.UpdateProfileRequest;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private User currentUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

    @GetMapping("/me")
    public User getMyProfile(Authentication authentication) {
        return currentUser(authentication);
    }

    @PutMapping("/me")
    public User updateMyProfile(Authentication authentication, @RequestBody UpdateProfileRequest req) {
        return userService.updateProfile(currentUser(authentication), req);
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(Authentication authentication, @RequestBody ChangePasswordRequest req) {
        userService.changePassword(currentUser(authentication), req);
        return ResponseEntity.ok().build();
    }

    // Danh cho ADMIN - xem danh sach khach hang/nguoi dung
    @GetMapping
    public Page<User> list(@RequestParam(required = false) String q,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        return userService.search(q, page, size);
    }
}
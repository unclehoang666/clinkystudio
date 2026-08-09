package com.example.backend.service.auth;

import com.example.backend.dto.*;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest req) {
        String username = req.getUsername() != null ? req.getUsername().trim() : "";
        String password = req.getPassword() != null ? req.getPassword() : "";

        if (username.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username không được để trống!");
        if (password.length() < 6)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu phải từ 6 ký tự trở lên!");
        if (userRepository.existsByUsername(username))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username đã tồn tại!");
        if (req.getEmail() != null && !req.getEmail().isBlank() && userRepository.existsByEmail(req.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã được sử dụng!");

        Role customerRole = roleRepository.findByCode("CUSTOMER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Chưa cấu hình role CUSTOMER trong DB"));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setRole(customerRole);
        user.setStatus(true);

        userRepository.save(user);
        return "Đăng ký thành công";
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai username hoặc password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai username hoặc password");

        if (Boolean.FALSE.equals(user.getStatus()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản đã bị khóa");

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().getCode());
        return new AuthResponse(token, user.getUsername(), user.getRole().getCode());
    }
}
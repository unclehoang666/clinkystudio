package com.example.backend.dto.staff;

import lombok.Data;

@Data
public class EmployeeCreateRequest {
    // Thong tin tai khoan dang nhap
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;

    // Thong tin cong viec
    private Integer positionId;
    private String role;   // ADMIN hoac STAFF
}
package com.example.backend.dto.staff;

import lombok.Data;

@Data
public class EmployeeUpdateRequest {
    private String fullName;
    private String email;
    private String phone;
    private Integer positionId;
    private Boolean status;
}
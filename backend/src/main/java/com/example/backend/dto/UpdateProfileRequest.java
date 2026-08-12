package com.example.backend.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String dateOfBirth; // format yyyy-MM-dd
}
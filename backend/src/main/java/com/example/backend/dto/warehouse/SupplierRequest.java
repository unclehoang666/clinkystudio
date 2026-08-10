package com.example.backend.dto.warehouse;

import lombok.Data;

@Data
public class SupplierRequest {
    private String code;
    private String name;
    private String phone;
    private String email;
    private String address;
    private Boolean status;
}
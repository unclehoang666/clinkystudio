package com.example.backend.entity.catalog;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "attributes")
@Data
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;           // vd: "Màu sắc", "Kích thước", "Dung lượng"

    @Column(nullable = false, unique = true, length = 50)
    private String code;           // vd: "color", "size", "storage"
}
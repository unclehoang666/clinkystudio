package com.example.backend.entity.catalog;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "attribute_values")
@Data
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @Column(nullable = false, length = 100)
    private String value;          // vd: "Đỏ", "M", "128GB"
}
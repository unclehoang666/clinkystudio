package com.example.backend.repository.catalog;

import com.example.backend.entity.catalog.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributeValueRepository extends JpaRepository<AttributeValue, Integer> {

    List<AttributeValue> findByAttribute_Id(Integer attributeId);

    boolean existsByAttribute_IdAndValueIgnoreCase(Integer attributeId, String value);
}
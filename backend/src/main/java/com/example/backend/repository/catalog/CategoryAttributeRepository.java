package com.example.backend.repository.catalog;

import com.example.backend.entity.catalog.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Integer> {

    List<CategoryAttribute> findByCategory_Id(Integer categoryId);

    boolean existsByCategory_IdAndAttribute_Id(Integer categoryId, Integer attributeId);

    void deleteByCategory_IdAndAttribute_Id(Integer categoryId, Integer attributeId);
}
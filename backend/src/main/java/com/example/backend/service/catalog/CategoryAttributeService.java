package com.example.backend.service.catalog;

import com.example.backend.dto.catalog.CategoryAttributeRequest;
import com.example.backend.entity.catalog.Attribute;
import com.example.backend.entity.catalog.Category;
import com.example.backend.entity.catalog.CategoryAttribute;
import com.example.backend.repository.catalog.CategoryAttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryAttributeService {

    private final CategoryAttributeRepository categoryAttributeRepository;
    private final CategoryService categoryService;
    private final AttributeService attributeService;

    // Tra ve danh sach thuoc tinh duoc phep dung cho 1 danh muc
    public List<CategoryAttribute> getByCategory(Integer categoryId) {
        return categoryAttributeRepository.findByCategory_Id(categoryId);
    }

    public CategoryAttribute assign(CategoryAttributeRequest req) {
        if (req.getCategoryId() == null || req.getAttributeId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải chọn cả danh mục và thuộc tính!");

        Category category = categoryService.getById(req.getCategoryId());
        Attribute attribute = attributeService.getById(req.getAttributeId());

        if (categoryAttributeRepository.existsByCategory_IdAndAttribute_Id(category.getId(), attribute.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Thuộc tính này đã được gán cho danh mục này rồi!");

        CategoryAttribute entity = new CategoryAttribute();
        entity.setCategory(category);
        entity.setAttribute(attribute);
        return categoryAttributeRepository.save(entity);
    }

    public void unassign(Integer categoryId, Integer attributeId) {
        if (!categoryAttributeRepository.existsByCategory_IdAndAttribute_Id(categoryId, attributeId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy liên kết này!");
        categoryAttributeRepository.deleteByCategory_IdAndAttribute_Id(categoryId, attributeId);
    }
}
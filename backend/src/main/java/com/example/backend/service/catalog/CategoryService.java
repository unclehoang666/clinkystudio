package com.example.backend.service.catalog;

import com.example.backend.dto.catalog.CategoryRequest;
import com.example.backend.entity.catalog.Category;
import com.example.backend.repository.catalog.CategoryRepository;
import com.example.backend.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private String generateCode() {
        return "CAT" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public Page<Category> search(String q, Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return categoryRepository.search(q, status, pageable);
    }

    public List<Category> getAllActive() {
        return categoryRepository.findByStatusTrue();
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    public List<Category> getChildren(Integer parentId) {
        return categoryRepository.findByParent_Id(parentId);
    }

    public Category getById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục"));
    }

    public Category create(CategoryRequest req) {
        String name = req.getName() != null ? req.getName().trim() : "";
        String code = (req.getCode() == null || req.getCode().trim().isEmpty())
                ? generateCode()
                : req.getCode().trim().toUpperCase();

        if (name.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên danh mục không được để trống!");
        if (name.length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên danh mục tối đa 100 ký tự!");
        if (code.length() > 20)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã danh mục tối đa 20 ký tự!");
        if (!code.matches("^[A-Z0-9_]*$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã chỉ được chứa chữ hoa, số và dấu gạch dưới (_)");
        if (categoryRepository.existsByNameIgnoreCase(name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên danh mục này đã tồn tại!");
        if (categoryRepository.existsByCodeIgnoreCase(code))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã danh mục này đã tồn tại!");

        Category entity = new Category();
        entity.setCode(code);
        entity.setName(name);
        entity.setDescription(req.getDescription());
        entity.setStatus(req.getStatus() != null ? req.getStatus() : true);
        entity.setCreatedAt(LocalDateTime.now());

        if (req.getParentId() != null) {
            Category parent = getById(req.getParentId());
            entity.setParent(parent);
        }

        return categoryRepository.save(entity);
    }

    public Category update(Integer id, CategoryRequest req) {
        Category entity = getById(id);

        String newName = req.getName() != null ? req.getName().trim() : "";
        if (newName.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên không được để trống!");
        if (newName.length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên tối đa 100 ký tự!");
        if (!entity.getName().equalsIgnoreCase(newName) && categoryRepository.existsByNameIgnoreCase(newName))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên này đã tồn tại ở danh mục khác!");

        if (req.getParentId() != null && req.getParentId().equals(id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh mục không thể tự làm cha của chính nó!");

        entity.setName(newName);
        if (req.getDescription() != null) entity.setDescription(req.getDescription());
        if (req.getStatus() != null) entity.setStatus(req.getStatus());
        entity.setParent(req.getParentId() != null ? getById(req.getParentId()) : null);
        entity.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(entity);
    }

    public void toggleStatus(Integer id) {
        Category entity = getById(id);
        if (Boolean.TRUE.equals(entity.getStatus()) && productRepository.existsByCategory_IdAndStatusTrue(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể ngừng hoạt động! Đang có sản phẩm thuộc danh mục này đang được bày bán.");
        }
        entity.setStatus(!entity.getStatus());
        entity.setUpdatedAt(LocalDateTime.now());
        categoryRepository.save(entity);
    }
}
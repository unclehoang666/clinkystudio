package com.example.backend.service.catalog;

import com.example.backend.dto.catalog.BrandRequest;
import com.example.backend.entity.catalog.Brand;
import com.example.backend.repository.catalog.BrandRepository;
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
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    private String generateCode() {
        return "BR" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public Page<Brand> search(String q, Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return brandRepository.search(q, status, pageable);
    }

    public List<Brand> getAllActive() {
        return brandRepository.findByStatusTrue();
    }

    public Brand getById(Integer id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu"));
    }

    public Brand create(BrandRequest req) {
        String name = req.getName() != null ? req.getName().trim() : "";
        String code = (req.getCode() == null || req.getCode().trim().isEmpty())
                ? generateCode()
                : req.getCode().trim().toUpperCase();

        if (name.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên thương hiệu không được để trống!");
        if (name.length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên thương hiệu tối đa 100 ký tự!");
        if (code.length() > 20)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã thương hiệu tối đa 20 ký tự!");
        if (!code.matches("^[A-Z0-9_]*$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã chỉ được chứa chữ hoa, số và dấu gạch dưới (_)");
        if (brandRepository.existsByNameIgnoreCase(name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên thương hiệu này đã tồn tại!");
        if (brandRepository.existsByCodeIgnoreCase(code))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã thương hiệu này đã tồn tại!");

        Brand entity = new Brand();
        entity.setCode(code);
        entity.setName(name);
        entity.setDescription(req.getDescription());
        entity.setStatus(req.getStatus() != null ? req.getStatus() : true);
        entity.setCreatedAt(LocalDateTime.now());

        return brandRepository.save(entity);
    }

    public Brand update(Integer id, BrandRequest req) {
        Brand entity = getById(id);

        String newName = req.getName() != null ? req.getName().trim() : "";
        if (newName.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên không được để trống!");
        if (newName.length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên tối đa 100 ký tự!");
        if (!entity.getName().equalsIgnoreCase(newName) && brandRepository.existsByNameIgnoreCase(newName))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên này đã tồn tại ở thương hiệu khác!");

        entity.setName(newName);
        if (req.getDescription() != null) entity.setDescription(req.getDescription());
        if (req.getStatus() != null) entity.setStatus(req.getStatus());
        entity.setUpdatedAt(LocalDateTime.now());

        return brandRepository.save(entity);
    }

    public void toggleStatus(Integer id) {
        Brand entity = getById(id);
        if (Boolean.TRUE.equals(entity.getStatus()) && productRepository.existsByBrand_IdAndStatusTrue(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể ngừng hoạt động! Đang có sản phẩm thuộc thương hiệu này đang được bày bán.");
        }
        entity.setStatus(!entity.getStatus());
        entity.setUpdatedAt(LocalDateTime.now());
        brandRepository.save(entity);
    }
}
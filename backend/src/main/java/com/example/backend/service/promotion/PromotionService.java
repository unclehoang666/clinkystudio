package com.example.backend.service.promotion;

import com.example.backend.dto.promotion.PromotionRequest;
import com.example.backend.entity.promotion.Promotion;
import com.example.backend.repository.promotion.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public Page<Promotion> search(Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return promotionRepository.search(status, pageable);
    }

    public Promotion getById(Integer id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy chương trình khuyến mãi"));
    }

    public Promotion create(PromotionRequest req) {
        String name = req.getName() != null ? req.getName().trim() : "";
        if (name.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên chương trình khuyến mãi không được để trống!");
        if (req.getType() == null || (!req.getType().equalsIgnoreCase("PERCENT") && !req.getType().equalsIgnoreCase("FIXED")))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại khuyến mãi phải là PERCENT hoặc FIXED!");
        if (req.getValue() == null || req.getValue().signum() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá trị khuyến mãi phải lớn hơn 0!");
        if ("PERCENT".equalsIgnoreCase(req.getType()) && req.getValue().compareTo(BigDecimal.valueOf(100)) > 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khuyến mãi theo % không được vượt quá 100!");
        if (req.getStartDate() == null || req.getEndDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải nhập ngày bắt đầu và kết thúc!");
        if (!req.getEndDate().isAfter(req.getStartDate()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày kết thúc phải sau ngày bắt đầu!");

        Promotion promotion = new Promotion();
        promotion.setCode((req.getCode() == null || req.getCode().isBlank()) ? generateCode() : req.getCode().toUpperCase());
        promotion.setName(name);
        promotion.setType(req.getType().toUpperCase());
        promotion.setValue(req.getValue());
        promotion.setStartDate(req.getStartDate());
        promotion.setEndDate(req.getEndDate());
        promotion.setAllowStacking(req.getAllowStacking() != null ? req.getAllowStacking() : false);
        promotion.setStatus(req.getStatus() != null ? req.getStatus() : true);

        return promotionRepository.save(promotion);
    }

    public Promotion update(Integer id, PromotionRequest req) {
        Promotion promotion = getById(id);

        if (req.getName() != null && !req.getName().isBlank()) promotion.setName(req.getName().trim());
        if (req.getValue() != null) {
            if (req.getValue().signum() <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá trị khuyến mãi phải lớn hơn 0!");
            promotion.setValue(req.getValue());
        }
        if (req.getStartDate() != null) promotion.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) promotion.setEndDate(req.getEndDate());
        if (req.getStartDate() != null && req.getEndDate() != null && !req.getEndDate().isAfter(req.getStartDate()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày kết thúc phải sau ngày bắt đầu!");
        if (req.getAllowStacking() != null) promotion.setAllowStacking(req.getAllowStacking());
        if (req.getStatus() != null) promotion.setStatus(req.getStatus());

        return promotionRepository.save(promotion);
    }

    public void toggleStatus(Integer id) {
        Promotion promotion = getById(id);
        promotion.setStatus(!promotion.getStatus());
        promotionRepository.save(promotion);
    }
}
package com.example.backend.service.promotion;

import com.example.backend.dto.promotion.PromotionProductRequest;
import com.example.backend.entity.product.Product;
import com.example.backend.entity.product.ProductVariant;
import com.example.backend.entity.promotion.Promotion;
import com.example.backend.entity.promotion.PromotionProduct;
import com.example.backend.repository.product.ProductRepository;
import com.example.backend.repository.product.ProductVariantRepository;
import com.example.backend.repository.promotion.PromotionProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionProductService {

    private final PromotionProductRepository promotionProductRepository;
    private final PromotionService promotionService;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public List<PromotionProduct> getByPromotion(Integer promotionId) {
        return promotionProductRepository.findByPromotion_Id(promotionId);
    }

    public PromotionProduct assign(PromotionProductRequest req) {
        if (req.getPromotionId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải chọn chương trình khuyến mãi!");
        if (req.getProductId() == null && req.getVariantId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải chọn sản phẩm hoặc biến thể để áp dụng khuyến mãi!");

        Promotion promotion = promotionService.getById(req.getPromotionId());

        PromotionProduct entity = new PromotionProduct();
        entity.setPromotion(promotion);

        if (req.getProductId() != null) {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));
            entity.setProduct(product);
        }
        if (req.getVariantId() != null) {
            ProductVariant variant = productVariantRepository.findById(req.getVariantId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể"));
            entity.setVariant(variant);
        }

        return promotionProductRepository.save(entity);
    }

    public void unassign(Integer id) {
        if (!promotionProductRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy liên kết khuyến mãi này");
        promotionProductRepository.deleteById(id);
    }

    // Tinh gia sau khuyen mai cho 1 variant (neu co khuyen mai dang hoat dong), dung cho trang chi tiet san pham
    public BigDecimal calculateDiscountedPrice(ProductVariant variant) {
        List<PromotionProduct> actives = promotionProductRepository.findActiveByVariantOrProduct(
                variant.getId(), variant.getProduct().getId(), LocalDateTime.now());

        if (actives.isEmpty()) return variant.getPrice();

        // Don gian: lay muc giam manh nhat trong cac khuyen mai dang hoat dong (khong cong don nhieu khuyen mai)
        BigDecimal bestPrice = variant.getPrice();
        for (PromotionProduct pp : actives) {
            Promotion promo = pp.getPromotion();
            BigDecimal discounted;
            if ("PERCENT".equalsIgnoreCase(promo.getType())) {
                discounted = variant.getPrice().subtract(
                        variant.getPrice().multiply(promo.getValue()).divide(BigDecimal.valueOf(100)));
            } else {
                discounted = variant.getPrice().subtract(promo.getValue());
            }
            if (discounted.signum() < 0) discounted = BigDecimal.ZERO;
            if (discounted.compareTo(bestPrice) < 0) bestPrice = discounted;
        }
        return bestPrice;
    }
}
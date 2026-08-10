package com.example.backend.service.product;

import com.example.backend.dto.product.ProductRequest;
import com.example.backend.dto.product.VariantRequest;
import com.example.backend.entity.catalog.AttributeValue;
import com.example.backend.entity.catalog.Brand;
import com.example.backend.entity.catalog.Category;
import com.example.backend.entity.product.*;
import com.example.backend.repository.catalog.AttributeValueRepository;
import com.example.backend.repository.catalog.BrandRepository;
import com.example.backend.repository.catalog.CategoryRepository;
import com.example.backend.repository.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantAttributeRepository productVariantAttributeRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final AttributeValueRepository attributeValueRepository;

    private String generateCode() {
        return "SP" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public Page<Product> search(String q, Integer categoryId, Integer brandId, Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return productRepository.search(q, categoryId, brandId, status, pageable);
    }

    public Product getById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));
    }

    public List<ProductImage> getImages(Integer productId) {
        return productImageRepository.findByProduct_IdOrderBySortOrderAsc(productId);
    }

    public List<ProductVariant> getVariants(Integer productId) {
        return productVariantRepository.findByProduct_Id(productId);
    }

    public List<ProductVariantAttribute> getVariantAttributes(Integer variantId) {
        return productVariantAttributeRepository.findByVariant_Id(variantId);
    }

    @Transactional
    public Product create(ProductRequest req) {
        String name = req.getName() != null ? req.getName().trim() : "";
        String code = (req.getCode() == null || req.getCode().trim().isEmpty())
                ? generateCode()
                : req.getCode().trim().toUpperCase();

        if (name.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên sản phẩm không được để trống!");
        if (productRepository.existsByNameIgnoreCase(name))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên sản phẩm này đã tồn tại!");
        if (productRepository.existsByCodeIgnoreCase(code))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã sản phẩm này đã tồn tại!");
        if (req.getVariants() == null || req.getVariants().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sản phẩm phải có ít nhất 1 biến thể (variant)!");

        Product product = new Product();
        product.setCode(code);
        product.setName(name);
        product.setDescription(req.getDescription());
        product.setMetaTitle(req.getMetaTitle());
        product.setMetaDescription(req.getMetaDescription());
        product.setStatus(req.getStatus() != null ? req.getStatus() : true);
        product.setIsGiveaway(req.getIsGiveaway() != null ? req.getIsGiveaway() : false);
        product.setCreatedAt(LocalDateTime.now());

        if (req.getBrandId() != null) {
            Brand brand = brandRepository.findById(req.getBrandId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu"));
            product.setBrand(brand);
        }
        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục"));
            product.setCategory(category);
        }

        Product saved = productRepository.save(product);

        // Luu anh
        if (req.getImageUrls() != null) {
            int order = 0;
            for (String url : req.getImageUrls()) {
                ProductImage img = new ProductImage();
                img.setProduct(saved);
                img.setUrl(url);
                img.setSortOrder(order++);
                productImageRepository.save(img);
            }
        }

        // Luu variant + thuoc tinh cua tung variant
        for (VariantRequest vReq : req.getVariants()) {
            saveVariant(saved, vReq);
        }

        return saved;
    }

    @Transactional
    public Product update(Integer id, ProductRequest req) {
        Product product = getById(id);

        String newName = req.getName() != null ? req.getName().trim() : "";
        if (newName.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên không được để trống!");
        if (!product.getName().equalsIgnoreCase(newName) && productRepository.existsByNameIgnoreCase(newName))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên này đã tồn tại ở sản phẩm khác!");

        product.setName(newName);
        if (req.getDescription() != null) product.setDescription(req.getDescription());
        if (req.getMetaTitle() != null) product.setMetaTitle(req.getMetaTitle());
        if (req.getMetaDescription() != null) product.setMetaDescription(req.getMetaDescription());
        if (req.getStatus() != null) product.setStatus(req.getStatus());
        if (req.getIsGiveaway() != null) product.setIsGiveaway(req.getIsGiveaway());
        product.setUpdatedAt(LocalDateTime.now());

        if (req.getBrandId() != null) {
            product.setBrand(brandRepository.findById(req.getBrandId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thương hiệu")));
        }
        if (req.getCategoryId() != null) {
            product.setCategory(categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục")));
        }

        Product saved = productRepository.save(product);

        // Cap nhat lai anh: xoa het anh cu, luu lai anh moi (don gian, du dung cho quy mo hien tai)
        if (req.getImageUrls() != null) {
            productImageRepository.deleteByProduct_Id(id);
            int order = 0;
            for (String url : req.getImageUrls()) {
                ProductImage img = new ProductImage();
                img.setProduct(saved);
                img.setUrl(url);
                img.setSortOrder(order++);
                productImageRepository.save(img);
            }
        }

        // Cap nhat variant: neu co id -> sua, neu khong co id -> tao moi
        if (req.getVariants() != null) {
            for (VariantRequest vReq : req.getVariants()) {
                if (vReq.getId() != null) {
                    updateVariant(vReq.getId(), vReq);
                } else {
                    saveVariant(saved, vReq);
                }
            }
        }

        return saved;
    }

    private void saveVariant(Product product, VariantRequest vReq) {
        String sku = vReq.getSku() != null ? vReq.getSku().trim() : "";
        if (sku.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SKU của biến thể không được để trống!");
        if (productVariantRepository.existsBySkuIgnoreCase(sku))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU '" + sku + "' đã tồn tại!");
        if (vReq.getPrice() == null || vReq.getPrice().signum() < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá biến thể không hợp lệ!");

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(sku);
        variant.setPrice(vReq.getPrice());
        variant.setQuantity(vReq.getQuantity() != null ? vReq.getQuantity() : 0);
        variant.setImageUrl(vReq.getImageUrl());
        variant.setStatus(vReq.getStatus() != null ? vReq.getStatus() : true);

        ProductVariant savedVariant = productVariantRepository.save(variant);

        if (vReq.getAttributes() != null) {
            for (var attrItem : vReq.getAttributes()) {
                AttributeValue attrValue = attributeValueRepository.findById(attrItem.getAttributeValueId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giá trị thuộc tính"));

                ProductVariantAttribute pva = new ProductVariantAttribute();
                pva.setVariant(savedVariant);
                pva.setAttributeValue(attrValue);
                productVariantAttributeRepository.save(pva);
            }
        }
    }

    private void updateVariant(Integer variantId, VariantRequest vReq) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy biến thể"));

        if (vReq.getSku() != null) {
            String newSku = vReq.getSku().trim();
            if (!variant.getSku().equalsIgnoreCase(newSku) && productVariantRepository.existsBySkuIgnoreCase(newSku))
                throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU '" + newSku + "' đã tồn tại!");
            variant.setSku(newSku);
        }
        if (vReq.getPrice() != null) variant.setPrice(vReq.getPrice());
        if (vReq.getQuantity() != null) variant.setQuantity(vReq.getQuantity());
        if (vReq.getImageUrl() != null) variant.setImageUrl(vReq.getImageUrl());
        if (vReq.getStatus() != null) variant.setStatus(vReq.getStatus());

        productVariantRepository.save(variant);

        // Gan lai thuoc tinh: xoa cu, them moi (don gian, du dung cho quy mo hien tai)
        if (vReq.getAttributes() != null) {
            productVariantAttributeRepository.deleteByVariant_Id(variantId);
            for (var attrItem : vReq.getAttributes()) {
                AttributeValue attrValue = attributeValueRepository.findById(attrItem.getAttributeValueId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giá trị thuộc tính"));

                ProductVariantAttribute pva = new ProductVariantAttribute();
                pva.setVariant(variant);
                pva.setAttributeValue(attrValue);
                productVariantAttributeRepository.save(pva);
            }
        }
    }

    public void toggleStatus(Integer id) {
        Product product = getById(id);
        product.setStatus(!product.getStatus());
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }
}
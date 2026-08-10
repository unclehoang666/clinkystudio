package com.example.backend.service.warehouse;

import com.example.backend.dto.warehouse.PurchaseOrderItemRequest;
import com.example.backend.dto.warehouse.PurchaseOrderRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.product.ProductVariant;
import com.example.backend.entity.warehouse.PurchaseOrder;
import com.example.backend.entity.warehouse.PurchaseOrderItem;
import com.example.backend.entity.warehouse.Supplier;
import com.example.backend.repository.product.ProductVariantRepository;
import com.example.backend.repository.warehouse.PurchaseOrderItemRepository;
import com.example.backend.repository.warehouse.PurchaseOrderRepository;
import com.example.backend.repository.warehouse.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final SupplierRepository supplierRepository;
    private final ProductVariantRepository productVariantRepository;

    private String generateCode() {
        return "PN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Page<PurchaseOrder> search(Integer supplierId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return purchaseOrderRepository.search(supplierId, status, pageable);
    }

    public PurchaseOrder getById(Integer id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu nhập"));
    }

    public List<PurchaseOrderItem> getItems(Integer purchaseOrderId) {
        return purchaseOrderItemRepository.findByPurchaseOrder_Id(purchaseOrderId);
    }

    // Tao phieu nhap o trang thai PENDING - CHUA cong vao ton kho
    @Transactional
    public PurchaseOrder create(User creator, PurchaseOrderRequest req) {
        if (req.getSupplierId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải chọn nhà cung cấp!");
        if (req.getItems() == null || req.getItems().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phiếu nhập phải có ít nhất 1 sản phẩm!");

        Supplier supplier = supplierRepository.findById(req.getSupplierId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhà cung cấp"));

        PurchaseOrder po = new PurchaseOrder();
        po.setCode(generateCode());
        po.setSupplier(supplier);
        po.setCreatedBy(creator);
        po.setNote(req.getNote());
        po.setStatus("PENDING");
        po.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        PurchaseOrder savedPo = purchaseOrderRepository.save(po);

        for (PurchaseOrderItemRequest itemReq : req.getItems()) {
            if (itemReq.getVariantId() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải chọn sản phẩm cho từng dòng nhập!");
            if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng nhập phải lớn hơn 0!");
            if (itemReq.getImportPrice() == null || itemReq.getImportPrice().signum() < 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giá nhập không hợp lệ!");

            ProductVariant variant = productVariantRepository.findById(itemReq.getVariantId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(savedPo);
            item.setVariant(variant);
            item.setQuantity(itemReq.getQuantity());
            item.setImportPrice(itemReq.getImportPrice());
            purchaseOrderItemRepository.save(item);

            total = total.add(itemReq.getImportPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
        }

        savedPo.setTotalAmount(total);
        return purchaseOrderRepository.save(savedPo);
    }

    // Xac nhan phieu nhap -> CONG vao ton kho that su (quantity), day la buoc duy nhat lam thay doi ton kho
    @Transactional
    public PurchaseOrder confirm(Integer id) {
        PurchaseOrder po = getById(id);

        if (!"PENDING".equals(po.getStatus()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể xác nhận phiếu đang ở trạng thái chờ xác nhận!");

        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByPurchaseOrder_Id(id);
        for (PurchaseOrderItem item : items) {
            ProductVariant variant = item.getVariant();
            variant.setQuantity(variant.getQuantity() + item.getQuantity());
            productVariantRepository.save(variant);
        }

        po.setStatus("CONFIRMED");
        po.setConfirmedAt(LocalDateTime.now());
        return purchaseOrderRepository.save(po);
    }

    // Huy phieu - chi duoc huy khi con PENDING (chua cong kho nen khong can hoan tac gi ca)
    @Transactional
    public PurchaseOrder cancel(Integer id) {
        PurchaseOrder po = getById(id);

        if (!"PENDING".equals(po.getStatus()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể hủy phiếu đang ở trạng thái chờ xác nhận!");

        po.setStatus("CANCELLED");
        return purchaseOrderRepository.save(po);
    }
}
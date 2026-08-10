package com.example.backend.service.returns;

import com.example.backend.dto.returns.ReturnRequestCreateRequest;
import com.example.backend.dto.returns.ReturnRequestItemRequest;
import com.example.backend.dto.returns.ReturnRequestProcessRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.order.Order;
import com.example.backend.entity.order.OrderItem;
import com.example.backend.entity.product.ProductVariant;
import com.example.backend.entity.returns.ReturnRequest;
import com.example.backend.entity.returns.ReturnRequestItem;
import com.example.backend.repository.order.OrderItemRepository;
import com.example.backend.repository.order.OrderRepository;
import com.example.backend.repository.product.ProductVariantRepository;
import com.example.backend.repository.returns.ReturnRequestItemRepository;
import com.example.backend.repository.returns.ReturnRequestRepository;
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
public class ReturnRequestService {

    private final ReturnRequestRepository returnRequestRepository;
    private final ReturnRequestItemRepository returnRequestItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;

    private String generateCode() {
        return "DT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Page<ReturnRequest> search(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return returnRequestRepository.search(status, pageable);
    }

    public List<ReturnRequest> getMyReturnRequests(User user) {
        return returnRequestRepository.findByOrder_User_IdOrderByCreatedAtDesc(user.getId());
    }

    public ReturnRequest getById(Integer id) {
        return returnRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đổi trả"));
    }

    public List<ReturnRequestItem> getItems(Integer returnRequestId) {
        return returnRequestItemRepository.findByReturnRequest_Id(returnRequestId);
    }

    @Transactional
    public ReturnRequest create(User user, ReturnRequestCreateRequest req) {
        if (req.getOrderId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu thông tin đơn hàng!");
        if (req.getRequestType() == null ||
                (!req.getRequestType().equalsIgnoreCase("EXCHANGE") && !req.getRequestType().equalsIgnoreCase("RETURN")))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loại yêu cầu phải là EXCHANGE hoặc RETURN!");
        if (req.getItems() == null || req.getItems().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải chọn ít nhất 1 sản phẩm để đổi/trả!");

        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        if (!order.getUser().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không thể tạo yêu cầu đổi trả cho đơn hàng không phải của mình!");

        if (order.getStatus() == null || !"COMPLETED".equals(order.getStatus().getCode()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể yêu cầu đổi trả với đơn hàng đã hoàn tất!");

        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setCode(generateCode());
        returnRequest.setOrder(order);
        returnRequest.setRequestType(req.getRequestType().toUpperCase());
        returnRequest.setReason(req.getReason());
        returnRequest.setStatus("PENDING");
        returnRequest.setCreatedAt(LocalDateTime.now());

        ReturnRequest saved = returnRequestRepository.save(returnRequest);

        for (ReturnRequestItemRequest itemReq : req.getItems()) {
            if (itemReq.getOrderItemId() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu thông tin sản phẩm cần đổi/trả!");
            if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng đổi/trả phải lớn hơn 0!");

            OrderItem orderItem = orderItemRepository.findById(itemReq.getOrderItemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm trong đơn hàng"));

            if (!orderItem.getOrder().getId().equals(order.getId()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sản phẩm này không thuộc đơn hàng đã chọn!");

            int maxReturnable = orderItem.getQuantity() - (orderItem.getReturnedQuantity() != null ? orderItem.getReturnedQuantity() : 0);
            if (itemReq.getQuantity() > maxReturnable)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Số lượng yêu cầu đổi/trả vượt quá số lượng có thể đổi trả (" + maxReturnable + ")!");

            ReturnRequestItem item = new ReturnRequestItem();
            item.setReturnRequest(saved);
            item.setOrderItem(orderItem);
            item.setQuantity(itemReq.getQuantity());
            item.setItemCondition(itemReq.getItemCondition());
            returnRequestItemRepository.save(item);
        }

        return saved;
    }

    // Admin duyet/tu choi/hoan tat yeu cau doi tra
    @Transactional
    public ReturnRequest process(User employee, Integer id, ReturnRequestProcessRequest req) {
        ReturnRequest returnRequest = getById(id);

        if (req.getStatus() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu trạng thái xử lý!");

        String newStatus = req.getStatus().toUpperCase();
        if (!List.of("APPROVED", "REJECTED", "COMPLETED").contains(newStatus))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ!");

        // Khi hoan tat (COMPLETED): cong lai ton kho cho cac san pham da tra (chi ap dung cho loai RETURN)
        if ("COMPLETED".equals(newStatus) && "RETURN".equals(returnRequest.getRequestType())) {
            List<ReturnRequestItem> items = returnRequestItemRepository.findByReturnRequest_Id(id);
            for (ReturnRequestItem item : items) {
                OrderItem orderItem = item.getOrderItem();
                ProductVariant variant = orderItem.getVariant();
                variant.setQuantity(variant.getQuantity() + item.getQuantity());
                productVariantRepository.save(variant);

                orderItem.setReturnedQuantity(
                        (orderItem.getReturnedQuantity() != null ? orderItem.getReturnedQuantity() : 0) + item.getQuantity());
                orderItemRepository.save(orderItem);
            }
        }

        returnRequest.setStatus(newStatus);
        returnRequest.setProcessedBy(employee);
        returnRequest.setProcessedAt(LocalDateTime.now());
        if (req.getRefundAmount() != null) returnRequest.setRefundAmount(req.getRefundAmount());
        if (req.getNote() != null) returnRequest.setNote(req.getNote());

        return returnRequestRepository.save(returnRequest);
    }
}
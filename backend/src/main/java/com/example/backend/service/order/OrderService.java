package com.example.backend.service.order;

import com.example.backend.dto.order.CheckoutRequest;
import com.example.backend.dto.order.UpdateOrderStatusRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.cart.Cart;
import com.example.backend.entity.cart.CartItem;
import com.example.backend.entity.order.Order;
import com.example.backend.entity.order.OrderItem;
import com.example.backend.entity.order.OrderStatus;
import com.example.backend.entity.order.OrderStatusHistory;
import com.example.backend.entity.product.ProductVariant;
import com.example.backend.entity.promotion.Coupon;
import com.example.backend.repository.cart.CartItemRepository;
import com.example.backend.repository.order.*;
import com.example.backend.repository.product.ProductVariantRepository;
import com.example.backend.repository.promotion.CouponRepository;
import com.example.backend.service.cart.CartService;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final CouponRepository couponRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartService cartService;

    private String generateCode() {
        return "DH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Page<Order> search(Integer userId, Integer statusId, String code, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return orderRepository.search(userId, statusId, code, pageable);
    }

    public List<Order> getMyOrders(User user) {
        return orderRepository.findByUser_IdOrderByCreatedAtDesc(user.getId());
    }

    public Order getById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));
    }

    public List<OrderItem> getItems(Integer orderId) {
        return orderItemRepository.findByOrder_Id(orderId);
    }

    public List<OrderStatusHistory> getHistory(Integer orderId) {
        return orderStatusHistoryRepository.findByOrder_IdOrderByCreatedAtAsc(orderId);
    }

    // ===================== CHECKOUT: tao don hang tu gio hang =====================
    @Transactional
    public Order checkout(User user, CheckoutRequest req) {
        Cart cart = cartService.getOrCreateCart(user);
        List<CartItem> cartItems = cartItemRepository.findByCart_Id(cart.getId());

        if (cartItems.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Giỏ hàng đang trống, không thể đặt hàng!");

        if (req.getReceiverName() == null || req.getReceiverName().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập tên người nhận!");
        if (req.getReceiverPhone() == null || req.getReceiverPhone().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập số điện thoại người nhận!");
        if (req.getShippingAddress() == null || req.getShippingAddress().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập địa chỉ giao hàng!");

        // Buoc 1: kiem tra ton kho cho tung item TRUOC khi tao don (fail fast, tranh tao don do dang giua chung)
        for (CartItem item : cartItems) {
            ProductVariant variant = item.getVariant();
            int available = variant.getQuantity() - variant.getReservedQuantity();
            if (item.getQuantity() > available) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Sản phẩm '" + variant.getSku() + "' không đủ tồn kho! Chỉ còn " + available + " sản phẩm.");
            }
        }

        // Buoc 2: tinh tam tinh (subtotal)
        BigDecimal subtotal = cartItems.stream()
                .map(i -> i.getVariant().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Buoc 3: ap coupon neu co
        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon coupon = null;
        if (req.getCouponCode() != null && !req.getCouponCode().isBlank()) {
            coupon = couponRepository.findByCodeIgnoreCase(req.getCouponCode().trim())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mã giảm giá không tồn tại!"));

            if (!Boolean.TRUE.equals(coupon.getStatus()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã giảm giá đã ngừng hoạt động!");
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã giảm giá không còn hiệu lực!");
            if (coupon.getUsedCount() >= coupon.getQuantity())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã giảm giá đã hết lượt sử dụng!");
            if (subtotal.compareTo(coupon.getMinOrderValue()) < 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Đơn hàng chưa đạt giá trị tối thiểu " + coupon.getMinOrderValue() + " để áp dụng mã này!");

            if ("PERCENT".equalsIgnoreCase(coupon.getDiscountType())) {
                discountAmount = subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
                if (coupon.getMaxDiscountValue() != null && discountAmount.compareTo(coupon.getMaxDiscountValue()) > 0) {
                    discountAmount = coupon.getMaxDiscountValue();
                }
            } else {
                discountAmount = coupon.getDiscountValue();
            }
        }

        BigDecimal shippingFee = BigDecimal.ZERO; // co the tinh theo dia chi sau, tam de 0
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(shippingFee);
        if (totalAmount.signum() < 0) totalAmount = BigDecimal.ZERO;

        OrderStatus pendingStatus = orderStatusRepository.findByCode("PENDING")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Chưa cấu hình trạng thái PENDING trong DB"));

        // Buoc 4: tao Order
        Order order = new Order();
        order.setCode(generateCode());
        order.setSubtotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalAmount);
        order.setReceiverName(req.getReceiverName().trim());
        order.setReceiverPhone(req.getReceiverPhone().trim());
        order.setShippingAddress(req.getShippingAddress().trim());
        order.setShippingWard(req.getShippingWard());
        order.setShippingDistrict(req.getShippingDistrict());
        order.setShippingProvince(req.getShippingProvince());
        order.setNote(req.getNote());
        order.setOrderType(0);
        order.setDeliveryMethod(req.getDeliveryMethod() != null ? req.getDeliveryMethod() : "HOME_DELIVERY");
        order.setUser(user);
        order.setCoupon(coupon);
        order.setStatus(pendingStatus);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Buoc 5: tao OrderItem + tru vao reserved_quantity (tam giu, chua tru thang vao quantity)
        for (CartItem item : cartItems) {
            ProductVariant variant = item.getVariant();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setVariant(variant);
            orderItem.setPrice(variant.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItemRepository.save(orderItem);

            variant.setReservedQuantity(variant.getReservedQuantity() + item.getQuantity());
            productVariantRepository.save(variant);
        }

        // Buoc 6: cap nhat luot dung coupon
        if (coupon != null) {
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }

        // Buoc 7: ghi lich su trang thai
        saveHistory(savedOrder, pendingStatus, "Đơn hàng được tạo");

        // Buoc 8: xoa gio hang
        cartItemRepository.deleteByCart_Id(cart.getId());

        return savedOrder;
    }

    // ===================== CAP NHAT TRANG THAI =====================
    @Transactional
    public Order updateStatus(Integer orderId, UpdateOrderStatusRequest req) {
        Order order = getById(orderId);

        OrderStatus newStatus = orderStatusRepository.findByCode(req.getStatusCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ!"));

        String oldCode = order.getStatus() != null ? order.getStatus().getCode() : null;
        String newCode = newStatus.getCode();

        // Khi don hang hoan tat (COMPLETED): tru that vao quantity, giai phong reserved
        if ("COMPLETED".equals(newCode) && !"COMPLETED".equals(oldCode)) {
            for (OrderItem item : orderItemRepository.findByOrder_Id(orderId)) {
                ProductVariant variant = item.getVariant();
                variant.setQuantity(variant.getQuantity() - item.getQuantity());
                variant.setReservedQuantity(variant.getReservedQuantity() - item.getQuantity());
                productVariantRepository.save(variant);
            }
            order.setPaidAt(LocalDateTime.now());
        }

        // Khi don hang bi huy (CANCELLED): giai phong reserved, khong tru quantity
        if ("CANCELLED".equals(newCode) && !"CANCELLED".equals(oldCode)) {
            for (OrderItem item : orderItemRepository.findByOrder_Id(orderId)) {
                ProductVariant variant = item.getVariant();
                variant.setReservedQuantity(Math.max(0, variant.getReservedQuantity() - item.getQuantity()));
                productVariantRepository.save(variant);
            }
        }

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);

        saveHistory(saved, newStatus, req.getNote());

        return saved;
    }

    private void saveHistory(Order order, OrderStatus status, String note) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setNote(note);
        history.setCreatedAt(LocalDateTime.now());
        orderStatusHistoryRepository.save(history);
    }
}
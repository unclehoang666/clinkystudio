package com.example.backend.service.cart;

import com.example.backend.dto.cart.CartItemRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.cart.Cart;
import com.example.backend.entity.cart.CartItem;
import com.example.backend.entity.product.ProductVariant;
import com.example.backend.repository.cart.CartItemRepository;
import com.example.backend.repository.cart.CartRepository;
import com.example.backend.repository.product.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;

    // Lay gio hang cua user, tu tao moi neu chua co (moi user luon co dung 1 cart)
    @Transactional
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setUpdatedAt(LocalDateTime.now());
                    return cartRepository.save(cart);
                });
    }

    public List<CartItem> getItems(User user) {
        Cart cart = getOrCreateCart(user);
        return cartItemRepository.findByCart_Id(cart.getId());
    }

    public BigDecimal getTotal(User user) {
        List<CartItem> items = getItems(user);
        return items.stream()
                .map(i -> i.getVariant().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public CartItem addItem(User user, CartItemRequest req) {
        if (req.getVariantId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phải chọn biến thể sản phẩm!");
        int qty = req.getQuantity() != null ? req.getQuantity() : 1;
        if (qty <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0!");

        ProductVariant variant = productVariantRepository.findById(req.getVariantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        if (Boolean.FALSE.equals(variant.getStatus()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sản phẩm này hiện không còn kinh doanh!");

        int available = variant.getQuantity() - variant.getReservedQuantity();

        Cart cart = getOrCreateCart(user);

        var existing = cartItemRepository.findByCart_IdAndVariant_Id(cart.getId(), variant.getId());

        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQuantity() + qty;
            if (newQty > available)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng trong kho không đủ! Chỉ còn " + available + " sản phẩm.");
            item.setQuantity(newQty);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
            return cartItemRepository.save(item);
        } else {
            if (qty > available)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng trong kho không đủ! Chỉ còn " + available + " sản phẩm.");
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setVariant(variant);
            item.setQuantity(qty);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
            return cartItemRepository.save(item);
        }
    }

    @Transactional
    public CartItem updateQuantity(User user, Integer variantId, Integer quantity) {
        if (quantity == null || quantity <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0! Nếu muốn xóa, dùng API xóa sản phẩm khỏi giỏ.");

        Cart cart = getOrCreateCart(user);
        CartItem item = cartItemRepository.findByCart_IdAndVariant_Id(cart.getId(), variantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sản phẩm không có trong giỏ hàng"));

        int available = item.getVariant().getQuantity() - item.getVariant().getReservedQuantity();
        if (quantity > available)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng trong kho không đủ! Chỉ còn " + available + " sản phẩm.");

        item.setQuantity(quantity);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return cartItemRepository.save(item);
    }

    @Transactional
    public void removeItem(User user, Integer variantId) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteByCart_IdAndVariant_Id(cart.getId(), variantId);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteByCart_Id(cart.getId());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
}
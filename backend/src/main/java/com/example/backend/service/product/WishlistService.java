package com.example.backend.service.product;

import com.example.backend.entity.User;
import com.example.backend.entity.product.Product;
import com.example.backend.entity.product.Wishlist;
import com.example.backend.repository.product.ProductRepository;
import com.example.backend.repository.product.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public List<Wishlist> getMyWishlist(User user) {
        return wishlistRepository.findByUser_Id(user.getId());
    }

    public Wishlist add(User user, Integer productId) {
        if (wishlistRepository.existsByUser_IdAndProduct_Id(user.getId(), productId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sản phẩm đã có trong danh sách yêu thích!");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);
        return wishlistRepository.save(wishlist);
    }

    public void remove(User user, Integer productId) {
        wishlistRepository.deleteByUser_IdAndProduct_Id(user.getId(), productId);
    }

    public boolean isWishlisted(User user, Integer productId) {
        return wishlistRepository.existsByUser_IdAndProduct_Id(user.getId(), productId);
    }
}
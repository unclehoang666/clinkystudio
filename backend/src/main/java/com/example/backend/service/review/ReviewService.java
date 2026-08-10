package com.example.backend.service.review;

import com.example.backend.dto.review.ReviewReplyRequest;
import com.example.backend.dto.review.ReviewRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.order.OrderItem;
import com.example.backend.entity.review.Review;
import com.example.backend.repository.order.OrderItemRepository;
import com.example.backend.repository.review.ReviewRepository;
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

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    public Page<Review> getByProduct(Integer productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return reviewRepository.findByProduct_Id(productId, pageable);
    }

    public List<Review> getMyReviews(User user) {
        return reviewRepository.findByUser_Id(user.getId());
    }

    public Review getById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đánh giá"));
    }

    // Chi duoc danh gia khi da co OrderItem tuong ung (tuc la da mua va nhan hang that su)
    public Review create(User user, ReviewRequest req) {
        if (req.getOrderItemId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu thông tin đơn hàng để đánh giá!");
        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số sao đánh giá phải từ 1 đến 5!");

        OrderItem orderItem = orderItemRepository.findById(req.getOrderItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm trong đơn hàng"));

        // Kiem tra dung nguoi mua thi moi duoc danh gia
        if (!orderItem.getOrder().getUser().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không thể đánh giá sản phẩm không thuộc đơn hàng của mình!");

        if (reviewRepository.existsByOrderItem_Id(req.getOrderItemId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sản phẩm này trong đơn hàng đã được đánh giá rồi!");

        Review review = new Review();
        review.setUser(user);
        review.setProduct(orderItem.getVariant().getProduct());
        review.setOrderItem(orderItem);
        review.setRating(req.getRating());
        review.setContent(req.getContent());
        review.setCategory(req.getCategory());
        review.setImages(req.getImages());
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    public Review update(User user, Long id, ReviewRequest req) {
        Review review = getById(id);

        if (!review.getUser().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không thể sửa đánh giá của người khác!");

        if (req.getRating() != null) {
            if (req.getRating() < 1 || req.getRating() > 5)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số sao đánh giá phải từ 1 đến 5!");
            review.setRating(req.getRating());
        }
        if (req.getContent() != null) review.setContent(req.getContent());
        if (req.getImages() != null) review.setImages(req.getImages());
        review.setIsEdited(true);

        return reviewRepository.save(review);
    }

    // Nhan vien/Admin tra loi danh gia
    public Review reply(User employee, Long id, ReviewReplyRequest req) {
        Review review = getById(id);

        if (req.getReplyContent() == null || req.getReplyContent().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nội dung trả lời không được để trống!");

        review.setRepliedBy(employee);
        review.setReplyContent(req.getReplyContent());
        review.setRepliedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    public void delete(User user, Long id) {
        Review review = getById(id);
        if (!review.getUser().getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không thể xóa đánh giá của người khác!");
        reviewRepository.delete(review);
    }
}
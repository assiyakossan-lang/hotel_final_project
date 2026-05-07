package com.hotel.service;

import com.hotel.dto.ReviewDTO;
import com.hotel.entity.Review;
import com.hotel.entity.User;
import com.hotel.exception.ResourceNotFoundException;
import com.hotel.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    public List<ReviewDTO.Response> getAllReviews() {
        return reviewRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ReviewDTO.Response> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ReviewDTO.Response getReviewById(Long id) {
        return toResponse(findReviewById(id));
    }

    @Transactional
    public ReviewDTO.Response createReview(ReviewDTO.Request request) {
        User user = userService.findUserById(request.getUserId());
        Review review = Review.builder()
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public ReviewDTO.Response updateReview(Long id, ReviewDTO.Request request) {
        Review review = findReviewById(id);
        if (request.getUserId() != null) {
            review.setUser(userService.findUserById(request.getUserId()));
        }
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }

    private Review findReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
    }

    private ReviewDTO.Response toResponse(Review review) {
        return ReviewDTO.Response.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

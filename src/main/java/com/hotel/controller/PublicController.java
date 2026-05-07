package com.hotel.controller;

import com.hotel.dto.BookingDTO;
import com.hotel.dto.ReviewDTO;
import com.hotel.dto.RoomDTO;
import com.hotel.service.BookingService;
import com.hotel.service.ReviewService;
import com.hotel.service.RoomService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {
    private final RoomService roomService;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDTO.Response>> rooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDTO.Response>> reviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/me/bookings")
    public ResponseEntity<List<BookingDTO.Response>> myBookings(HttpSession session) {
        Long userId = currentUserId(session);
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    @PostMapping("/me/bookings")
    public ResponseEntity<BookingDTO.Response> createMyBooking(@Valid @RequestBody MyBookingRequest request,
                                                               HttpSession session) {
        Long userId = currentUserId(session);
        BookingDTO.Request bookingRequest = BookingDTO.Request.builder()
                .userId(userId)
                .roomId(request.getRoomId())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(bookingRequest));
    }

    @PostMapping("/me/reviews")
    public ResponseEntity<ReviewDTO.Response> createMyReview(@Valid @RequestBody MyReviewRequest request,
                                                             HttpSession session) {
        Long userId = currentUserId(session);
        ReviewDTO.Request reviewRequest = new ReviewDTO.Request(userId, request.getRating(), request.getComment());
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(reviewRequest));
    }

    @DeleteMapping("/me/reviews/{id}")
    public ResponseEntity<Void> deleteMyReview(@PathVariable Long id, HttpSession session) {
        Long userId = currentUserId(session);
        ReviewDTO.Response review = reviewService.getReviewById(id);
        if (!review.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/bookings/{id}/cancel")
    public ResponseEntity<BookingDTO.Response> cancelMyBooking(@PathVariable Long id, HttpSession session) {
        Long userId = currentUserId(session);
        BookingDTO.Response booking = bookingService.getBookingById(id);
        if (!booking.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @Data
    public static class MyBookingRequest {
        @NotNull(message = "Room ID is required")
        private Long roomId;

        @NotNull(message = "Check-in date is required")
        @FutureOrPresent(message = "Check-in date must not be in the past")
        private LocalDate checkInDate;

        @NotNull(message = "Check-out date is required")
        @Future(message = "Check-out date must be in the future")
        private LocalDate checkOutDate;
    }

    @Data
    public static class MyReviewRequest {
        @NotNull(message = "Rating is required")
        private Integer rating;
        @NotNull(message = "Comment is required")
        private String comment;
    }

    private Long currentUserId(HttpSession session) {
        Object id = session == null ? null : session.getAttribute(AuthController.SESSION_USER_ID);
        if (id == null) {
            throw new IllegalArgumentException("Login required");
        }
        return (Long) id;
    }
}

package com.hotel.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotNull(message = "User ID is required")
        private Long userId;

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
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long userId;
        private String userName;
        private Long roomId;
        private String roomNumber;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private BigDecimal totalPrice;
        private String status;
        private LocalDateTime createdAt;
    }
}

package com.hotel.dto;

import com.hotel.entity.Room;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

public class RoomDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "Room number is required")
        private String roomNumber;

        @NotNull(message = "Room type is required")
        private Room.RoomType type;

        @Min(value = 1, message = "Capacity must be at least 1")
        private int capacity;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be positive")
        private BigDecimal pricePerNight;

        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String roomNumber;
        private Room.RoomType type;
        private int capacity;
        private BigDecimal pricePerNight;
        private String description;
        private boolean available;
    }
}

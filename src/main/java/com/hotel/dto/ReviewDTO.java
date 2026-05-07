package com.hotel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

public class ReviewDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long userId;

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be from 1 to 5")
        @Max(value = 5, message = "Rating must be from 1 to 5")
        private Integer rating;

        @NotBlank(message = "Comment is required")
        private String comment;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long userId;
        private String userName;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;
    }
}

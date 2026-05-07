package com.hotel.config;

import com.hotel.entity.Review;
import com.hotel.entity.Room;
import com.hotel.entity.User;
import com.hotel.repository.ReviewRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public void run(String... args) {
        if (roomRepository.count() == 0) {
            log.info("Initializing room data...");
            List<Room> rooms = List.of(
                Room.builder().roomNumber("101").type(Room.RoomType.STANDARD).capacity(2)
                    .pricePerNight(new BigDecimal("15000"))
                    .description("Standard room with city view").available(true).build(),
                Room.builder().roomNumber("102").type(Room.RoomType.STANDARD).capacity(2)
                    .pricePerNight(new BigDecimal("16000"))
                    .description("Standard room on a quiet floor").available(true).build(),
                Room.builder().roomNumber("201").type(Room.RoomType.DELUXE).capacity(2)
                    .pricePerNight(new BigDecimal("25000"))
                    .description("Deluxe room with king-size bed").available(true).build(),
                Room.builder().roomNumber("202").type(Room.RoomType.DELUXE).capacity(3)
                    .pricePerNight(new BigDecimal("30000"))
                    .description("Deluxe family room with extra bed").available(true).build(),
                Room.builder().roomNumber("301").type(Room.RoomType.SUITE).capacity(4)
                    .pricePerNight(new BigDecimal("50000"))
                    .description("Suite with living area and panoramic view").available(true).build()
            );
            roomRepository.saveAll(rooms);
        }

        if (userRepository.count() == 0) {
            log.info("Initializing accounts...");
            userRepository.saveAll(List.of(
                User.builder()
                    .name("Admin")
                    .email("admin@hotel.kz")
                    .phone("+7 700 000 00 01")
                    .password("admin123")
                    .role(User.Role.ADMIN)
                    .build(),
                User.builder()
                    .name("User")
                    .email("user@hotel.kz")
                    .phone("+7 700 000 00 02")
                    .password("user123")
                    .role(User.Role.USER)
                    .build()
            ));
        }

        if (reviewRepository.count() == 0) {
            User user = userRepository.findByEmail("user@hotel.kz").orElse(null);
            User admin = userRepository.findByEmail("admin@hotel.kz").orElse(null);
            if (user != null && admin != null) {
                reviewRepository.saveAll(List.of(
                    Review.builder().user(user).rating(5).comment("Rooms are clean and the booking process is simple.").build(),
                    Review.builder().user(admin).rating(5).comment("Comfortable hotel with fast support and good prices.").build()
                ));
            }
        }
    }
}

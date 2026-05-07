package com.hotel.service;

import com.hotel.dto.BookingDTO;
import com.hotel.entity.Booking;
import com.hotel.entity.Room;
import com.hotel.entity.User;
import com.hotel.exception.ResourceNotFoundException;
import com.hotel.exception.RoomNotAvailableException;
import com.hotel.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final RoomService roomService;

    public List<BookingDTO.Response> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BookingDTO.Response getBookingById(Long id) {
        return toResponse(findBookingById(id));
    }

    public List<BookingDTO.Response> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDTO.Response createBooking(BookingDTO.Request request) {
        // 1. Validate dates
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // 2. Find user and room (throws 404 if not found)
        User user = userService.findUserById(request.getUserId());
        Room room = roomService.findRoomById(request.getRoomId());

        // 3. Check room is enabled
        if (!room.isAvailable()) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + " is not available");
        }

        // 4. Check for overlapping bookings
        boolean hasOverlap = bookingRepository.existsOverlappingBooking(
                room.getId(), request.getCheckInDate(), request.getCheckOutDate()
        );
        if (hasOverlap) {
            throw new RoomNotAvailableException(
                    "Room " + room.getRoomNumber() + " is already booked for the selected dates"
            );
        }

        // 5. Calculate total price
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        // 6. Save booking
        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .totalPrice(totalPrice)
                .status(Booking.BookingStatus.CONFIRMED)
                .build();

        return toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingDTO.Response cancelBooking(Long id) {
        Booking booking = findBookingById(id);
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }
        if (booking.getStatus() == Booking.BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel a completed booking");
        }
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
    }

    private BookingDTO.Response toResponse(Booking b) {
        return BookingDTO.Response.builder()
                .id(b.getId())
                .userId(b.getUser().getId())
                .userName(b.getUser().getName())
                .roomId(b.getRoom().getId())
                .roomNumber(b.getRoom().getRoomNumber())
                .checkInDate(b.getCheckInDate())
                .checkOutDate(b.getCheckOutDate())
                .totalPrice(b.getTotalPrice())
                .status(b.getStatus().name())
                .createdAt(b.getCreatedAt())
                .build();
    }
}

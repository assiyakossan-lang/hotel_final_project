package com.hotel.repository;

import com.hotel.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByRoomId(Long roomId);

    List<Booking> findByStatus(Booking.BookingStatus status);

    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        WHERE b.room.id = :roomId
        AND b.status = 'CONFIRMED'
        AND NOT (b.checkOutDate <= :checkIn OR b.checkInDate >= :checkOut)
    """)
    boolean existsOverlappingBooking(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}

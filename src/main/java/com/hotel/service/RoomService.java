package com.hotel.service;

import com.hotel.dto.RoomDTO;
import com.hotel.entity.Room;
import com.hotel.exception.ResourceNotFoundException;
import com.hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;

    public List<RoomDTO.Response> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RoomDTO.Response getRoomById(Long id) {
        return toResponse(findRoomById(id));
    }

    public List<RoomDTO.Response> getAvailableRooms() {
        return roomRepository.findByAvailableTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<RoomDTO.Response> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        return roomRepository.findAvailableRooms(checkIn, checkOut).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomDTO.Response createRoom(RoomDTO.Request request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new IllegalArgumentException("Room number already exists: " + request.getRoomNumber());
        }
        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .type(request.getType())
                .capacity(request.getCapacity())
                .pricePerNight(request.getPricePerNight())
                .description(request.getDescription())
                .available(true)
                .build();
        return toResponse(roomRepository.save(room));
    }

    @Transactional
    public RoomDTO.Response updateRoom(Long id, RoomDTO.Request request) {
        Room room = findRoomById(id);
        if (!room.getRoomNumber().equals(request.getRoomNumber())
                && roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new IllegalArgumentException("Room number already exists: " + request.getRoomNumber());
        }
        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getType());
        room.setCapacity(request.getCapacity());
        room.setPricePerNight(request.getPricePerNight());
        room.setDescription(request.getDescription());
        return toResponse(roomRepository.save(room));
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room not found with id: " + id);
        }
        roomRepository.deleteById(id);
    }

    public Room findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    public RoomDTO.Response toResponse(Room room) {
        return RoomDTO.Response.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType())
                .capacity(room.getCapacity())
                .pricePerNight(room.getPricePerNight())
                .description(room.getDescription())
                .available(room.isAvailable())
                .build();
    }
}

package com.hotel.service;

import com.hotel.dto.UserDTO;
import com.hotel.entity.User;
import com.hotel.exception.ResourceNotFoundException;
import com.hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO.Response> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserDTO.Response getUserById(Long id) {
        return toResponse(findUserById(id));
    }

    @Transactional
    public UserDTO.Response createUser(UserDTO.Request request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }
        User.Role role = parseRole(request.getRole());
        String password = request.getPassword() == null || request.getPassword().isBlank()
                ? "user123"
                : request.getPassword();
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(password)
                .role(role)
                .build();
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserDTO.Response register(String name, String email, String phone, String password, String role) {
        UserDTO.Request request = UserDTO.Request.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .password(password)
                .role(role == null || role.isBlank() ? "USER" : role)
                .build();
        return createUser(request);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return user;
    }

    @Transactional
    public UserDTO.Response updateUser(Long id, UserDTO.Request request) {
        User user = findUserById(id);
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        }
        if (request.getRole() != null && !request.getRole().isBlank()) {
            user.setRole(parseRole(request.getRole()));
        }
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public UserDTO.Response toResponse(User user) {
        return UserDTO.Response.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private User.Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            return User.Role.USER;
        }
        try {
            return User.Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role must be USER or ADMIN");
        }
    }
}

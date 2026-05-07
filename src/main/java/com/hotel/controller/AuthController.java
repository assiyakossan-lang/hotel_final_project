package com.hotel.controller;

import com.hotel.dto.AuthDTO;
import com.hotel.dto.UserDTO;
import com.hotel.entity.User;
import com.hotel.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    public static final String SESSION_USER_ID = "USER_ID";
    public static final String SESSION_USER_NAME = "USER_NAME";
    public static final String SESSION_USER_EMAIL = "USER_EMAIL";
    public static final String SESSION_USER_ROLE = "USER_ROLE";

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthDTO.SessionResponse> register(@Valid @RequestBody AuthDTO.RegisterRequest request,
                                                            HttpSession session) {
        UserDTO.Response user = userService.register(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword(),
                request.getRole()
        );
        setSession(session, user.getId(), user.getName(), user.getEmail(), user.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(toSessionResponse(session));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDTO.SessionResponse> login(@Valid @RequestBody AuthDTO.LoginRequest request,
                                                         HttpSession session) {
        User user = userService.login(request.getEmail(), request.getPassword());
        setSession(session, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(toSessionResponse(session));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthDTO.SessionResponse> me(HttpSession session) {
        return ResponseEntity.ok(toSessionResponse(session));
    }

    private void setSession(HttpSession session, Long id, String name, String email, String role) {
        session.setAttribute(SESSION_USER_ID, id);
        session.setAttribute(SESSION_USER_NAME, name);
        session.setAttribute(SESSION_USER_EMAIL, email);
        session.setAttribute(SESSION_USER_ROLE, role);
    }

    private AuthDTO.SessionResponse toSessionResponse(HttpSession session) {
        Object id = session.getAttribute(SESSION_USER_ID);
        if (id == null) {
            return AuthDTO.SessionResponse.builder().authenticated(false).build();
        }
        return AuthDTO.SessionResponse.builder()
                .authenticated(true)
                .id((Long) id)
                .name((String) session.getAttribute(SESSION_USER_NAME))
                .email((String) session.getAttribute(SESSION_USER_EMAIL))
                .role((String) session.getAttribute(SESSION_USER_ROLE))
                .build();
    }
}

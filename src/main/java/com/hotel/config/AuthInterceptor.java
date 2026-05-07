package com.hotel.config;

import com.hotel.controller.AuthController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        HttpSession session = request.getSession(false);
        String role = session == null ? null : (String) session.getAttribute(AuthController.SESSION_USER_ROLE);

        if (path.equals("/admin.html") || path.equals("/api-links.html")) {
            if (!"ADMIN".equals(role)) {
                response.sendRedirect("/login.html");
                return false;
            }
        }

        if (path.startsWith("/api/")) {
            if (path.startsWith("/api/auth/") || path.startsWith("/api/public/")) {
                return true;
            }
            if (role == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Login required\"}");
                return false;
            }
            if (!"ADMIN".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Admin access required\"}");
                return false;
            }
        }
        return true;
    }
}

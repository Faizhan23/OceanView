package com.oceanviewresort.controller;

import com.oceanviewresort.model.User;
import com.oceanviewresort.service.AuthService;
import com.oceanviewresort.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Optional;

/**
 * Servlet Controller for Login and Logout.
 * MVC – Controller: handles HTTP, delegates to AuthService, forwards to JSP view.
 * No business logic here. No SQL here.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {

    private static final String SESSION_USER = "loggedInUser";
    private AuthService authService;

    @Override
    public void init() {
        this.authService = new AuthService();
    }

    /** Display login page */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            handleLogout(req, resp);
            return;
        }

        // If already logged in – redirect to dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(SESSION_USER) != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
    }

    /** Process login form submission */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = ValidationUtil.sanitise(req.getParameter("username"));
        String password = req.getParameter("password"); // not sanitised – raw for BCrypt

        try {
            Optional<User> userOpt = authService.authenticate(username, password);

            if (userOpt.isPresent()) {
                // Prevent session fixation
                req.getSession(false);
                HttpSession session = req.getSession(true);
                session.setAttribute(SESSION_USER, userOpt.get());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                req.setAttribute("errorMessage", "Invalid username or password.");
                req.setAttribute("username", username);
                req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
            }

        } catch (IllegalArgumentException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("username", username);
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login?msg=logged_out");
    }
}

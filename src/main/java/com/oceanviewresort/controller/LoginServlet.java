// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.controller;

import com.oceanviewresort.model.User;
import com.oceanviewresort.service.AuthService;
import com.oceanviewresort.util.ValidationUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@WebServlet(
   name = "LoginServlet",
   urlPatterns = {"/login", "/logout"}
)
public class LoginServlet extends HttpServlet {
   private static final String SESSION_USER = "loggedInUser";
   private AuthService authService;

   public LoginServlet() {
   }

   public void init() {
      this.authService = new AuthService();
   }

   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String path = req.getServletPath();
      if ("/logout".equals(path)) {
         this.handleLogout(req, resp);
      } else {
         HttpSession session = req.getSession(false);
         if (session != null && session.getAttribute("loggedInUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
         } else {
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
         }
      }
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      String username = ValidationUtil.sanitise(req.getParameter("username"));
      String password = req.getParameter("password");

      try {
         Optional<User> userOpt = this.authService.authenticate(username, password);
         if (userOpt.isPresent()) {
            req.getSession(false);
            HttpSession session = req.getSession(true);
            session.setAttribute("loggedInUser", userOpt.get());
            session.setMaxInactiveInterval(1800);
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

   private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      HttpSession session = req.getSession(false);
      if (session != null) {
         session.invalidate();
      }

      resp.sendRedirect(req.getContextPath() + "/login?msg=logged_out");
   }
}

// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(
   urlPatterns = {"/dashboard/*", "/reservation/*", "/billing/*", "/reports/*", "/help/*"}
)
public class AuthFilter implements Filter {
   private static final String LOGIN_URL = "/login";
   private static final String SESSION_USER = "loggedInUser";

   public AuthFilter() {
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      HttpServletRequest req = (HttpServletRequest)request;
      HttpServletResponse resp = (HttpServletResponse)response;
      HttpSession session = req.getSession(false);
      boolean isLoggedIn = session != null && session.getAttribute("loggedInUser") != null;
      if (isLoggedIn) {
         chain.doFilter(request, response);
      } else {
         String contextPath = req.getContextPath();
         resp.sendRedirect(contextPath + "/login?error=session_expired");
      }

   }
}

// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.controller;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(
   urlPatterns = {"/*"}
)
public class EncodingFilter implements Filter {
   private String encoding = "UTF-8";

   public EncodingFilter() {
   }

   public void init(FilterConfig config) {
      String enc = config.getInitParameter("encoding");
      if (enc != null && !enc.isBlank()) {
         this.encoding = enc;
      }

   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      request.setCharacterEncoding(this.encoding);
      response.setCharacterEncoding(this.encoding);
      chain.doFilter(request, response);
   }
}

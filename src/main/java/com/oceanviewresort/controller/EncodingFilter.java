// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.oceanviewresort.controller;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
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

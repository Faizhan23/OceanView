<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // Redirect root URL to the login servlet
    response.sendRedirect(request.getContextPath() + "/LoginServlet");
%>

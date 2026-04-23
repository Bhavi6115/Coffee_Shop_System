package com.coffee.controller;

import com.coffee.util.DBConnect;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/updateOrderStatus")
public class UpdateOrderStatusServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        String status = request.getParameter("status");

        try (Connection conn = DBConnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE orders SET status=? WHERE id=?");
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        response.sendRedirect("admin");
    }
}
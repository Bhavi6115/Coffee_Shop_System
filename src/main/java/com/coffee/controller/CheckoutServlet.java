package com.coffee.controller;

import com.coffee.util.DBConnect;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");

        if (userId == null) {
            response.sendRedirect("login.html");
            return;
        }
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect("menu");
            return;
        }

        String payment = request.getParameter("payment");
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            // Calculate total
            double total = 0.0;
            String priceQuery = "SELECT price FROM products WHERE id=?";
            PreparedStatement priceStmt = conn.prepareStatement(priceQuery);
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                priceStmt.setInt(1, entry.getKey());
                ResultSet rs = priceStmt.executeQuery();
                if (rs.next()) {
                    total += rs.getDouble("price") * entry.getValue();
                }
            }

            // Insert order
            String orderSql = "INSERT INTO orders (user_id, total_amount) VALUES (?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId);
            orderStmt.setDouble(2, total);
            orderStmt.executeUpdate();
            ResultSet keys = orderStmt.getGeneratedKeys();
            keys.next();
            int orderId = keys.getInt(1);

            // Insert order items
            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSql);
            for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
                priceStmt.setInt(1, entry.getKey());
                ResultSet rs = priceStmt.executeQuery();
                if (rs.next()) {
                    double price = rs.getDouble("price");
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, entry.getKey());
                    itemStmt.setInt(3, entry.getValue());
                    itemStmt.setDouble(4, price);
                    itemStmt.addBatch();
                }
            }
            itemStmt.executeBatch();

            // Insert bill
            String billSql = "INSERT INTO bills (order_id, payment_method) VALUES (?, ?)";
            PreparedStatement billStmt = conn.prepareStatement(billSql);
            billStmt.setInt(1, orderId);
            billStmt.setString(2, payment);
            billStmt.executeUpdate();

            conn.commit();

            // Clear cart
            session.removeAttribute("cart");

            // Redirect to order confirmation (generate HTML)
            response.sendRedirect("orderConfirmation?orderId=" + orderId);

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            response.sendRedirect("viewCart?error=checkout");
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
}
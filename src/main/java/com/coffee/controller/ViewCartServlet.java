package com.coffee.controller;

import com.coffee.util.DBConnect;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

@WebServlet("/viewCart")
public class ViewCartServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Shopping Cart • Bean Haven</title>");
        out.println("<style>");
        out.println("* { margin:0; padding:0; box-sizing:border-box; }");
        out.println("body { font-family:'Segoe UI', sans-serif; background:#faf7f2; }");
        out.println(".navbar { background:#4e3620; color:white; padding:1rem 2rem; display:flex; justify-content:space-between; align-items:center; }");
        out.println(".navbar a { color:#f3e9de; margin-left:1.5rem; text-decoration:none; font-weight:500; }");
        out.println(".brand { font-size:1.8rem; font-weight:bold; }");
        out.println(".container { max-width:1000px; margin:2rem auto; padding:0 1.5rem; }");
        out.println("h2 { color:#3a2a1c; margin-bottom:1.5rem; border-left:6px solid #b87c4d; padding-left:1.2rem; }");
        out.println(".empty-cart { background:white; border-radius:20px; padding:3rem; text-align:center; box-shadow:0 8px 20px rgba(0,0,0,0.04); }");
        out.println(".empty-cart p { font-size:1.2rem; margin-bottom:1.5rem; color:#666; }");
        out.println(".btn { background:#7b4f2e; color:white; padding:0.8rem 2rem; border-radius:30px; text-decoration:none; font-weight:600; display:inline-block; }");
        out.println(".cart-table-container { background:white; border-radius:20px; padding:1.5rem; box-shadow:0 8px 20px rgba(0,0,0,0.04); }");
        out.println(".cart-table { width:100%; border-collapse:collapse; }");
        out.println(".cart-table th { text-align:left; padding:1rem 0.5rem; border-bottom:2px solid #eee; color:#5a3e2b; font-weight:600; }");
        out.println(".cart-table td { padding:1rem 0.5rem; border-bottom:1px solid #f0e0d0; vertical-align:middle; }");
        out.println(".item-info { display:flex; align-items:center; gap:10px; }");
        out.println(".item-icon { font-size:2rem; }");
        out.println(".qty-form { display:flex; align-items:center; gap:6px; }");
        out.println(".qty-input { width:60px; padding:6px; border:1px solid #ddd; border-radius:20px; text-align:center; }");
        out.println(".btn-update { background:#e0d0c0; border:none; padding:5px 12px; border-radius:20px; cursor:pointer; font-size:0.8rem; }");
        out.println(".btn-remove { color:#b33c3c; text-decoration:none; font-size:0.9rem; margin-left:10px; }");
        out.println(".total-row { font-weight:bold; font-size:1.2rem; }");
        out.println(".checkout-box { background:white; border-radius:20px; padding:1.8rem; margin-top:2rem; box-shadow:0 8px 20px rgba(0,0,0,0.04); }");
        out.println(".checkout-title { font-size:1.5rem; margin-bottom:1.5rem; color:#3a2a1c; }");
        out.println(".payment-options { display:flex; gap:20px; margin:1.5rem 0; }");
        out.println(".payment-option { display:flex; align-items:center; gap:8px; }");
        out.println(".btn-checkout { background:#7b4f2e; color:white; border:none; padding:0.9rem 2.5rem; border-radius:30px; font-weight:600; font-size:1rem; cursor:pointer; }");
        out.println(".continue-link { display:inline-block; margin-top:1rem; color:#8b5e3c; text-decoration:none; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        // Navbar
        out.println("<div class='navbar'>");
        out.println("<span class='brand'>&#9749; Bean Haven</span>");
        out.println("<div>");
        out.println("👤 " + session.getAttribute("username"));
        out.println("<a href='menu'>Menu</a>");
        out.println("<a href='orders'>My Orders</a>");
        out.println("<a href='viewCart'>&#128722; Cart</a>");
        if ("admin".equals(session.getAttribute("role"))) {
            out.println("<a href='admin'>Admin</a>");
        }
        out.println("<a href='logout'>Logout</a>");
        out.println("</div></div>");

        out.println("<div class='container'>");
        out.println("<h2>&#128722; Your Shopping Cart</h2>");

        if (cart == null || cart.isEmpty()) {
            out.println("<div class='empty-cart'>");
            out.println("<p>&#129530; Your cart is empty</p>");
            out.println("<a href='menu' class='btn'>☕ Browse Menu</a>");
            out.println("</div>");
        } else {
            out.println("<div class='cart-table-container'>");
            out.println("<table class='cart-table'>");
            out.println("<tr><th>Item</th><th>Price</th><th>Quantity</th><th>Subtotal</th><th></th></tr>");
            double total = 0.0;

            try (Connection conn = DBConnect.getConnection()) {
                StringBuilder inClause = new StringBuilder();
                for (int i = 0; i < cart.size(); i++) inClause.append("?,");
                String sql = "SELECT id, name, price FROM products WHERE id IN (" +
                        inClause.substring(0, inClause.length()-1) + ")";
                PreparedStatement stmt = conn.prepareStatement(sql);
                int idx = 1;
                for (Integer id : cart.keySet()) {
                    stmt.setInt(idx++, id);
                }
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    int qty = cart.get(id);
                    double subtotal = price * qty;
                    total += subtotal;

                    // Icon based on name
                    String icon = "☕";
                    if (name.toLowerCase().contains("iced")) icon = "🧊";
                    else if (name.toLowerCase().contains("croissant")) icon = "🥐";
                    else if (name.toLowerCase().contains("mocha")) icon = "🍫";
                    else if (name.toLowerCase().contains("caramel")) icon = "🍯";
                    else if (name.toLowerCase().contains("samosa")) icon = "🥟";
                    else if (name.toLowerCase().contains("vada")) icon = "🍔";

                    out.println("<tr>");
                    out.println("<td>");
                    out.println("<div class='item-info'>");
                    out.println("<span class='item-icon'>" + icon + "</span>");
                    out.println("<span>" + name + "</span>");
                    out.println("</div>");
                    out.println("</td>");
                    out.println("<td>₹" + String.format("%.2f", price) + "</td>");
                    out.println("<td>");
                    out.println("<form method='post' action='updateCart' class='qty-form'>");
                    out.println("<input type='hidden' name='productId' value='" + id + "'>");
                    out.println("<input type='number' name='quantity' value='" + qty + "' min='1' class='qty-input'>");
                    out.println("<button type='submit' class='btn-update'>Update</button>");
                    out.println("</form>");
                    out.println("</td>");
                    out.println("<td>₹" + String.format("%.2f", subtotal) + "</td>");
                    out.println("<td><a href='removeFromCart?productId=" + id + "' class='btn-remove'>❌ Remove</a></td>");
                    out.println("</tr>");
                }
                out.println("<tr class='total-row'>");
                out.println("<td colspan='3' style='text-align:right;'>Total:</td>");
                out.println("<td>₹" + String.format("%.2f", total) + "</td>");
                out.println("<td></td>");
                out.println("</tr>");
                out.println("</table>");
                out.println("</div>");

                // Checkout Section
                out.println("<div class='checkout-box'>");
                out.println("<div class='checkout-title'>💳 Checkout</div>");
                out.println("<form method='post' action='checkout'>");
                out.println("<div class='payment-options'>");
                out.println("<label class='payment-option'><input type='radio' name='payment' value='Cash' checked> 💵 Cash</label>");
                out.println("<label class='payment-option'><input type='radio' name='payment' value='Card'> 💳 Card</label>");
                out.println("<label class='payment-option'><input type='radio' name='payment' value='UPI'> 📱 UPI</label>");
                out.println("</div>");
                out.println("<button type='submit' class='btn-checkout'>✅ Place Order</button>");
                out.println("</form>");
                out.println("</div>");
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("<p style='color:red;'>Error loading cart.</p>");
            }
        }

        out.println("<a href='menu' class='continue-link'>← Continue Shopping</a>");
        out.println("</div>"); // container
        out.println("</body></html>");
    }
}
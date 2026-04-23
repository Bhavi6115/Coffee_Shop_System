package com.coffee.controller;

import com.coffee.util.DBConnect;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }
        int userId = (Integer) session.getAttribute("userId");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>My Orders</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', sans-serif; background: #faf7f2; }");
        out.println(".navbar { background: #4e3620; color: white; padding: 1rem 2rem; display: flex; justify-content: space-between; }");
        out.println(".navbar a { color: #f3e9de; margin-left: 1.5rem; text-decoration: none; }");
        out.println(".container { max-width: 1200px; margin: 2rem auto; padding: 0 1rem; }");
        out.println(".order-table { width: 100%; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }");
        out.println(".order-table th { background: #6b4f3a; color: white; padding: 1rem; }");
        out.println(".order-table td { padding: 1rem; border-bottom: 1px solid #eee; }");
        out.println(".status { padding: 0.3rem 0.8rem; border-radius: 20px; font-size: 0.8rem; font-weight: 600; }");
        out.println(".status-pending { background: #fef3c7; color: #92400e; }");
        out.println(".status-completed { background: #d1fae5; color: #065f46; }");
        out.println(".status-cancelled { background: #fee2e2; color: #991b1b; }");
        out.println(".btn-view { background: #8b5e3c; color: white; padding: 0.4rem 1rem; border-radius: 20px; text-decoration: none; font-size: 0.9rem; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        // Navbar
        out.println("<div class='navbar'>");
        out.println("<span style='font-size:1.5rem; font-weight:bold;'>&#9749; Bean Haven</span>");
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
        out.println("<h2 style='color:#3a2a1c; margin-bottom:1.5rem;'>&#128221; My Orders</h2>");

        try (Connection conn = DBConnect.getConnection()) {
            String sql = "SELECT o.id, o.order_date, o.total_amount, o.status, b.payment_method " +
                    "FROM orders o LEFT JOIN bills b ON o.id = b.order_id " +
                    "WHERE o.user_id = ? ORDER BY o.order_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            out.println("<table class='order-table'>");
            out.println("<tr><th>Order ID</th><th>Date</th><th>Total</th><th>Status</th><th>Payment</th><th>Action</th></tr>");
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm");
            boolean hasOrders = false;
            while (rs.next()) {
                hasOrders = true;
                int orderId = rs.getInt("id");
                String status = rs.getString("status");
                String paymentMethod = rs.getString("payment_method");
                String statusClass = "status-" + status;

                out.println("<tr>");
                out.println("<td>#" + orderId + "</td>");
                out.println("<td>" + sdf.format(rs.getTimestamp("order_date")) + "</td>");
                out.println("<td>₹" + String.format("%.2f", rs.getDouble("total_amount")) + "</td>");
                out.println("<td><span class='status " + statusClass + "'>" +
                        (status.equals("pending") ? "⏳ Pending" :
                                status.equals("completed") ? "✅ Completed" : "❌ Cancelled") +
                        "</span></td>");
                out.println("<td>" + (paymentMethod != null ?
                        (paymentMethod.equals("Cash") ? "💵 Cash" :
                                paymentMethod.equals("Card") ? "💳 Card" : "📱 UPI") : "—") + "</td>");
                out.println("<td><a href='bill?orderId=" + orderId + "' class='btn-view'>View Bill</a></td>");
                out.println("</tr>");
            }
            if (!hasOrders) {
                out.println("<tr><td colspan='6' style='text-align:center; padding:2rem;'>No orders yet. <a href='menu'>Order now</a></td></tr>");
            }
            out.println("</table>");
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p style='color:red;'>Error loading orders.</p>");
        }

        out.println("<p style='margin-top:1.5rem;'><a href='menu' style='color:#8b5e3c;'>&larr; Back to Menu</a></p>");
        out.println("</div>");
        out.println("</body></html>");
    }
}
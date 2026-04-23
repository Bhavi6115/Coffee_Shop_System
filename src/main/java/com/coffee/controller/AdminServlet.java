package com.coffee.controller;

import com.coffee.util.DBConnect;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Admin Dashboard</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', sans-serif; background: #faf7f2; }");
        out.println(".navbar { background: #4e3620; color: white; padding: 1rem 2rem; display: flex; justify-content: space-between; }");
        out.println(".navbar a { color: #f3e9de; margin-left: 1.5rem; text-decoration: none; }");
        out.println(".container { max-width: 1300px; margin: 2rem auto; padding: 0 1rem; }");
        out.println(".admin-table { width: 100%; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }");
        out.println(".admin-table th { background: #6b4f3a; color: white; padding: 1rem; }");
        out.println(".admin-table td { padding: 0.8rem 1rem; border-bottom: 1px solid #eee; }");
        out.println(".status-badge { padding: 0.3rem 0.8rem; border-radius: 20px; font-size: 0.8rem; font-weight: 600; }");
        out.println(".status-pending { background: #fef3c7; color: #92400e; }");
        out.println(".status-completed { background: #d1fae5; color: #065f46; }");
        out.println(".status-cancelled { background: #fee2e2; color: #991b1b; }");
        out.println(".action-link { margin-right: 10px; color: #8b5e3c; text-decoration: none; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<div class='navbar'>");
        out.println("<span style='font-size:1.5rem;'>&#128274; Admin Panel</span>");
        out.println("<div>");
        out.println("👤 " + session.getAttribute("username") + " (Admin)");
        out.println("<a href='menu'>Menu</a>");
        out.println("<a href='orders'>Orders</a>");
        out.println("<a href='admin'>Admin</a>");
        out.println("<a href='logout'>Logout</a>");
        out.println("</div></div>");

        out.println("<div class='container'>");
        out.println("<h2 style='color:#3a2a1c;'>&#128202; All Orders</h2>");

        try (Connection conn = DBConnect.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT o.id, u.full_name, o.order_date, o.total_amount, o.status FROM orders o JOIN users u ON o.user_id = u.id ORDER BY o.order_date DESC");
            out.println("<table class='admin-table'>");
            out.println("<tr><th>Order ID</th><th>Customer</th><th>Date</th><th>Total</th><th>Status</th><th>Actions</th></tr>");
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm");
            while (rs.next()) {
                int orderId = rs.getInt("id");
                String status = rs.getString("status");
                out.println("<tr>");
                out.println("<td>#" + orderId + "</td>");
                out.println("<td>" + rs.getString("full_name") + "</td>");
                out.println("<td>" + sdf.format(rs.getTimestamp("order_date")) + "</td>");
                out.println("<td>₹" + String.format("%.2f", rs.getDouble("total_amount")) + "</td>");
                out.println("<td><span class='status-badge status-" + status + "'>" + status + "</span></td>");
                out.println("<td>");
                if (!"completed".equals(status) && !"cancelled".equals(status)) {
                    out.println("<a href='updateOrderStatus?orderId=" + orderId + "&status=completed' class='action-link'>✅ Complete</a>");
                    out.println("<a href='updateOrderStatus?orderId=" + orderId + "&status=cancelled' class='action-link'>❌ Cancel</a>");
                }
                out.println("<a href='bill?orderId=" + orderId + "' class='action-link'>📄 Bill</a>");
                out.println("</td>");
                out.println("</tr>");
            }
            out.println("</table>");
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p>Error loading orders.</p>");
        }
        out.println("</div>");
        out.println("</body></html>");
    }
}
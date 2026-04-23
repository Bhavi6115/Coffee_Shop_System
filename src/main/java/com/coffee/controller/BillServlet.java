package com.coffee.controller;

import com.coffee.util.DBConnect;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;

@WebServlet("/bill")
public class BillServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderIdParam = request.getParameter("orderId");
        if (orderIdParam == null) {
            response.sendRedirect("orders");
            return;
        }
        int orderId = Integer.parseInt(orderIdParam);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Bill #" + orderId + "</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', sans-serif; background: #faf7f2; margin:0; }");
        out.println(".navbar { background: #4e3620; color: white; padding: 1rem 2rem; display: flex; justify-content: space-between; }");
        out.println(".navbar a { color: #f3e9de; text-decoration: none; }");
        out.println(".container { max-width: 800px; margin: 2rem auto; background: white; border-radius: 16px; padding: 2rem; box-shadow: 0 8px 20px rgba(0,0,0,0.05); }");
        out.println(".bill-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px dashed #ccc; padding-bottom: 1rem; }");
        out.println(".bill-title { font-size: 1.8rem; color: #3a2a1c; }");
        out.println(".print-btn { background: #ddd; border: none; padding: 0.5rem 1.5rem; border-radius: 20px; cursor: pointer; }");
        out.println(".bill-details { margin: 1.5rem 0; }");
        out.println(".bill-table { width: 100%; border-collapse: collapse; }");
        out.println(".bill-table th { text-align: left; padding: 0.5rem; border-bottom: 1px solid #eee; }");
        out.println(".bill-table td { padding: 0.8rem 0.5rem; border-bottom: 1px solid #eee; }");
        out.println(".total-row { font-weight: bold; font-size: 1.2rem; }");
        out.println("@media print { .navbar, .print-btn, .back-link { display: none; } .container { box-shadow: none; } }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<div class='navbar'>");
        out.println("<span style='font-size:1.5rem;'>&#9749; Bean Haven</span>");
        out.println("<a href='orders'>← Back to Orders</a>");
        out.println("</div>");

        out.println("<div class='container'>");
        try (Connection conn = DBConnect.getConnection()) {
            // Order info
            String orderSql = "SELECT o.order_date, o.total_amount, o.status, u.full_name, b.payment_method " +
                    "FROM orders o JOIN users u ON o.user_id = u.id " +
                    "LEFT JOIN bills b ON o.id = b.order_id WHERE o.id = ?";
            PreparedStatement stmt = conn.prepareStatement(orderSql);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                out.println("<div class='bill-header'>");
                out.println("<div class='bill-title'>&#127873; Bill #" + orderId + "</div>");
                out.println("<button class='print-btn' onclick='window.print()'>&#128424; Print</button>");
                out.println("</div>");

                out.println("<div class='bill-details'>");
                out.println("<p><strong>Date:</strong> " + new SimpleDateFormat("dd MMM yyyy, HH:mm").format(rs.getTimestamp("order_date")) + "</p>");
                out.println("<p><strong>Customer:</strong> " + rs.getString("full_name") + "</p>");
                out.println("<p><strong>Payment:</strong> " +
                        (rs.getString("payment_method") != null ?
                                (rs.getString("payment_method").equals("Cash") ? "💵 Cash" :
                                        rs.getString("payment_method").equals("Card") ? "💳 Card" : "📱 UPI") : "—") + "</p>");
                out.println("<p><strong>Status:</strong> " +
                        (rs.getString("status").equals("completed") ? "✅ Completed" :
                                rs.getString("status").equals("pending") ? "⏳ Pending" : "❌ Cancelled") + "</p>");
                out.println("</div>");

                // Items
                String itemSql = "SELECT p.name, oi.quantity, oi.price FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
                PreparedStatement itemStmt = conn.prepareStatement(itemSql);
                itemStmt.setInt(1, orderId);
                ResultSet itemRs = itemStmt.executeQuery();

                out.println("<table class='bill-table'>");
                out.println("<tr><th>Item</th><th>Qty</th><th>Price</th><th>Subtotal</th></tr>");
                double total = 0;
                while (itemRs.next()) {
                    double price = itemRs.getDouble("price");
                    int qty = itemRs.getInt("quantity");
                    double subtotal = price * qty;
                    total += subtotal;
                    out.println("<tr>");
                    out.println("<td>" + itemRs.getString("name") + "</td>");
                    out.println("<td>" + qty + "</td>");
                    out.println("<td>₹" + String.format("%.2f", price) + "</td>");
                    out.println("<td>₹" + String.format("%.2f", subtotal) + "</td>");
                    out.println("</tr>");
                }
                out.println("<tr class='total-row'><td colspan='3' style='text-align:right;'>Total:</td><td>₹" + String.format("%.2f", total) + "</td></tr>");
                out.println("</table>");

                out.println("<p style='margin-top:2rem; text-align:center; color:#888;'>&#9749; Thank you for visiting!</p>");
            } else {
                out.println("<p>Order not found.</p>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p>Error loading bill.</p>");
        }
        out.println("<p class='back-link'><a href='orders' style='color:#8b5e3c;'>&larr; Back to Orders</a></p>");
        out.println("</div>");
        out.println("</body></html>");
    }
}
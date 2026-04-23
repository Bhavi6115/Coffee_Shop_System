package com.coffee.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/orderConfirmation")
public class OrderConfirmationServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderId = request.getParameter("orderId");
        if (orderId == null) {
            response.sendRedirect("menu");
            return;
        }

        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : "Customer";

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Order Confirmed • Bean Haven</title>");
        out.println("<style>");
        out.println("* { margin:0; padding:0; box-sizing:border-box; }");
        out.println("body { font-family:'Segoe UI', sans-serif; background:#faf7f2; }");
        out.println(".navbar { background:#4e3620; color:white; padding:1rem 2rem; display:flex; justify-content:space-between; align-items:center; }");
        out.println(".navbar a { color:#f3e9de; margin-left:1.5rem; text-decoration:none; font-weight:500; }");
        out.println(".brand { font-size:1.8rem; font-weight:bold; }");
        out.println(".container { max-width:700px; margin:3rem auto; padding:0 1.5rem; }");
        out.println(".confirmation-card { background:white; border-radius:30px; padding:2.5rem; box-shadow:0 15px 30px rgba(0,0,0,0.08); text-align:center; }");
        out.println(".success-icon { font-size:5rem; margin-bottom:1rem; }");
        out.println("h1 { color:#2e6b2e; margin-bottom:0.5rem; }");
        out.println(".order-number { background:#f0e0d0; display:inline-block; padding:0.5rem 2rem; border-radius:40px; font-size:1.5rem; font-weight:bold; color:#3a2a1c; margin:1.5rem 0; }");
        out.println(".message { color:#555; margin-bottom:2rem; font-size:1.1rem; }");
        out.println(".button-group { display:flex; gap:1rem; justify-content:center; flex-wrap:wrap; }");
        out.println(".btn-primary { background:#7b4f2e; color:white; padding:0.9rem 2rem; border-radius:40px; text-decoration:none; font-weight:600; display:inline-block; }");
        out.println(".btn-secondary { background:transparent; color:#7b4f2e; border:2px solid #7b4f2e; padding:0.9rem 2rem; border-radius:40px; text-decoration:none; font-weight:600; display:inline-block; }");
        out.println(".footer-note { margin-top:2rem; color:#999; font-size:0.9rem; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        // Navbar
        out.println("<div class='navbar'>");
        out.println("<span class='brand'>&#9749; Bean Haven</span>");
        out.println("<div>");
        out.println("👤 " + username);
        out.println("<a href='menu'>Menu</a>");
        out.println("<a href='orders'>My Orders</a>");
        out.println("<a href='viewCart'>&#128722; Cart</a>");
        if (session != null && "admin".equals(session.getAttribute("role"))) {
            out.println("<a href='admin'>Admin</a>");
        }
        out.println("<a href='logout'>Logout</a>");
        out.println("</div></div>");

        out.println("<div class='container'>");
        out.println("<div class='confirmation-card'>");
        out.println("<div class='success-icon'>✅🎉</div>");
        out.println("<h1>Order Confirmed!</h1>");
        out.println("<p class='message'>Thank you for your order, " + username + "!<br>Your coffee is being prepared with love.</p>");
        out.println("<div class='order-number'>Order #" + orderId + "</div>");
        out.println("<div class='button-group'>");
        out.println("<a href='menu' class='btn-primary'>☕ Order More</a>");
        out.println("<a href='orders' class='btn-secondary'>📋 View My Orders</a>");
        out.println("</div>");
        out.println("<p class='footer-note'>You can track your order status in 'My Orders'.</p>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body></html>");
    }
}
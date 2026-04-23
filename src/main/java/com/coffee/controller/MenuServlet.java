package com.coffee.controller;

import com.coffee.util.DBConnect;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/menu")
public class MenuServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Coffee Shop • Menu</title>");
        out.println("<style>");
        out.println("* { margin:0; padding:0; box-sizing:border-box; }");
        out.println("body { font-family:'Segoe UI', sans-serif; background:#faf7f2; }");
        out.println(".navbar { background:#4e3620; color:white; padding:1rem 2rem; display:flex; justify-content:space-between; align-items:center; }");
        out.println(".navbar a { color:#f3e9de; margin-left:1.5rem; text-decoration:none; font-weight:500; }");
        out.println(".brand { font-size:1.8rem; font-weight:bold; }");
        out.println(".container { max-width:1300px; margin:2rem auto; padding:0 1.5rem; }");
        out.println(".toast { background:#2e6b2e; color:white; padding:0.7rem 1.8rem; border-radius:30px; display:inline-block; margin-bottom:1.5rem; }");
        out.println(".menu-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:2rem; }");
        out.println(".menu-header h2 { font-size:2rem; color:#3a2a1c; border-left:6px solid #b87c4d; padding-left:1.2rem; }");
        out.println(".cart-badge { background:#8b5e3c; color:white; padding:0.5rem 1.2rem; border-radius:30px; font-weight:600; text-decoration:none; }");
        out.println(".menu-grid { display:grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap:1.8rem; }");
        out.println(".coffee-card { background:white; border-radius:20px; box-shadow:0 8px 20px rgba(0,0,0,0.04); padding:1.5rem 1.2rem; border:1px solid #f1e5d8; transition: transform 0.2s, box-shadow 0.2s; position:relative; display:flex; flex-direction:column; }");
        out.println(".coffee-card:hover { transform:translateY(-5px); box-shadow:0 16px 28px rgba(0,0,0,0.08); border-color:#d4b69a; }");
        out.println(".card-icon { font-size:3rem; text-align:center; margin-bottom:0.8rem; }");
        out.println(".card-title { font-size:1.4rem; font-weight:700; color:#3a2a1c; margin-bottom:0.3rem; }");
        out.println(".card-desc { color:#6b5b4e; font-size:0.9rem; margin-bottom:1rem; line-height:1.4; }");
        out.println(".rating { display:flex; align-items:center; gap:4px; margin:0.5rem 0; }");
        out.println(".stars { color:#ffb443; font-size:1rem; }");
        out.println(".rating-value { color:#8a6e5a; font-size:0.8rem; }");
        out.println(".card-price { font-size:1.8rem; font-weight:700; color:#7b4f2e; margin:0.5rem 0 1.2rem; }");
        out.println(".badge-popular { position:absolute; top:12px; right:12px; background:#d4a373; color:#2e1b0e; font-size:0.7rem; font-weight:600; padding:0.2rem 0.7rem; border-radius:20px; }");
        out.println(".cart-form { display:flex; gap:6px; margin-top:auto; }");
        out.println(".qty-input { width:55px; padding:6px; border:1px solid #ccc; border-radius:20px; text-align:center; }");
        out.println(".btn-add { flex:1; background:#7b4f2e; color:white; border:none; border-radius:30px; padding:8px; font-weight:600; cursor:pointer; }");
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

        if ("1".equals(request.getParameter("added"))) {
            out.println("<div class='toast'>✅ Item added to cart!</div>");
        }

        out.println("<div class='menu-header'>");
        out.println("<h2>Our Menu</h2>");
        out.println("<a href='viewCart' class='cart-badge'>&#128722; View Cart</a>");
        out.println("</div>");

        out.println("<div class='menu-grid'>");

        try (Connection conn = DBConnect.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products ORDER BY category, name");
            int index = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String desc = rs.getString("description");
                double price = switch (name) {
                    case "Espresso" -> 25.00;
                    case "Cappuccino" -> 35.00;
                    case "Latte" -> 40.00;
                    case "Iced Coffee" -> 30.00;
                    case "Croissant" -> 22.00;
                    case "Mocha" -> 45.00;
                    case "Americano" -> 28.00;
                    case "Caramel Macchiato" -> 50.00;
                    case "Cold Brew" -> 38.00;
                    case "Masala Chai" -> 25.00;
                    case "Filter Coffee" -> 30.00;
                    case "Samosa" -> 20.00;
                    case "Vada Pav" -> 35.00;
                    default -> rs.getDouble("price"); // fallback to DB value

                };

                String category = rs.getString("category");
                index++;

                String icon = "☕";
                if (name.toLowerCase().contains("iced") || category.toLowerCase().contains("cold")) icon = "🧊";
                else if (name.toLowerCase().contains("croissant")) icon = "🥐";
                else if (name.toLowerCase().contains("mocha")) icon = "🍫";
                else if (name.toLowerCase().contains("caramel")) icon = "🍯";

                double rating = 4.2 + (index * 0.1);
                if (rating > 5.0) rating = 5.0;
                int fullStars = (int) rating;
                boolean halfStar = (rating - fullStars) >= 0.5;

                boolean popular = (name.equals("Cappuccino") || name.equals("Latte") || name.equals("Mocha"));

                out.println("<div class='coffee-card'>");
                if (popular) {
                    out.println("<div class='badge-popular'>🔥 Popular</div>");
                }
                out.println("<div class='card-icon'>" + icon + "</div>");
                out.println("<div class='card-title'>" + name + "</div>");
                out.println("<div class='card-desc'>" + desc + "</div>");

                out.println("<div class='rating'>");
                out.println("<span class='stars'>");
                for (int i = 0; i < fullStars; i++) out.print("★");
                if (halfStar) out.print("½");
                for (int i = fullStars + (halfStar ? 1 : 0); i < 5; i++) out.print("☆");
                out.println("</span>");
                out.println("<span class='rating-value'>" + String.format("%.1f", rating) + "</span>");
                out.println("</div>");

                out.println("<div class='card-price'>₹" + String.format("%.2f", price) + "</div>");

                out.println("<form method='post' action='cart' class='cart-form'>");
                out.println("<input type='hidden' name='productId' value='" + id + "'>");
                out.println("<input type='number' name='quantity' value='1' min='1' class='qty-input'>");
                out.println("<button type='submit' class='btn-add'>Add</button>");
                out.println("</form>");

                out.println("</div>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p style='color:red;'>Error loading menu.</p>");
        }

        out.println("</div>"); // menu-grid
        out.println("</div>"); // container
        out.println("</body></html>");
    }
}
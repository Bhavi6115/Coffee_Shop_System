package com.coffee.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

@WebServlet("/updateCart")
public class UpdateCartServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart != null) {
            try {
                int productId = Integer.parseInt(request.getParameter("productId"));
                int quantity = Integer.parseInt(request.getParameter("quantity"));

                if (quantity > 0) {
                    cart.put(productId, quantity);
                } else {
                    cart.remove(productId);
                }
                session.setAttribute("cart", cart);
            } catch (NumberFormatException e) {
                // Invalid input, ignore
            }
        }
        response.sendRedirect("viewCart");
    }
}
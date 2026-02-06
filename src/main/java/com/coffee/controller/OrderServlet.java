package com.coffee.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/order")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String coffee = request.getParameter("coffee");
        String quantity = request.getParameter("quantity");
        String price = request.getParameter("price");

        System.out.println("Coffee: " + coffee);
        System.out.println("Quantity: " + quantity);
        System.out.println("Price: " + price);

        response.getWriter().println("Order received successfully!");
    }
}

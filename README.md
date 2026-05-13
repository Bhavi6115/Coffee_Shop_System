# ☕ Coffee Shop POS System

A web-based Point of Sale (POS) system for coffee shops, built using Java Servlets, JDBC, MySQL, and Apache Tomcat. It provides separate interfaces for customers and admins – customers can browse the menu, manage a cart, place orders, and view order history; admins can view all orders, update order statuses, and generate bills.

---

## 📌 Features

- 🔐 **User Authentication** – Login with admin/employee roles and session management  
- 📋 **Dynamic Menu** – Menu items fetched from MySQL with category icons and ratings  
- 🛒 **Shopping Cart** – Add, update quantity, remove items (session-based)  
- 🧾 **Order Placement** – Checkout with payment method selection (Cash/Card/UPI)  
- 📦 **Order Management** – Admin dashboard to view and update order status  
- 🖨️ **Printable Bills** – Detailed invoices with itemized lists and totals  
- 📱 **Responsive UI** – Clean, modern interface with emoji-enhanced visuals  

---

## 🛠️ Tech Stack

| Layer       | Technology                         |
|-------------|------------------------------------|
| Frontend    | HTML5, CSS3 (inline styling)       |
| Backend     | Java Servlets (Java EE)            |
| Database    | MySQL 8.0                          |
| Server      | Apache Tomcat 9.x                  |
| Build/IDE   | IntelliJ IDEA (manual JAR mgmt)    |
| Connector   | MySQL Connector/J 8.0.35 (JDBC)    |

---

## 🚀 Setup & Installation

### 1. Clone the Repository
```bash
git clone https://github.com/Bhavi6115/coffee-shop-pos.git

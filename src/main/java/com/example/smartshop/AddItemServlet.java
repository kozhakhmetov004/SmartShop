package com.example.smartshop;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/addItem")
public class AddItemServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String itemName = request.getParameter("itemName");
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String listUuid = request.getParameter("listUuid"); // <-- from form (hidden input)

        String jdbcURL = "jdbc:mysql://localhost:3306/smartshop";
        String dbUser = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            // 1. Find list_id using UUID
            String findListSql = "SELECT id FROM lists WHERE uuid = ?";
            PreparedStatement findListStmt = connection.prepareStatement(findListSql);
            findListStmt.setString(1, listUuid);
            ResultSet rs = findListStmt.executeQuery();

            int listId = -1;
            if (rs.next()) {
                listId = rs.getInt("id");
            } else {
                throw new Exception("List not found!");
            }
            rs.close();
            findListStmt.close();

            // 2. Insert item into items table
            String insertItemSql = "INSERT INTO items (name, quantity, list_id) VALUES (?, ?, ?)";
            PreparedStatement insertItemStmt = connection.prepareStatement(insertItemSql);
            insertItemStmt.setString(1, itemName);
            insertItemStmt.setInt(2, quantity);
            insertItemStmt.setInt(3, listId);
            insertItemStmt.executeUpdate();

            insertItemStmt.close();
            connection.close();

            response.sendRedirect("index.jsp?listId=" + listUuid); // Ensure the query parameter is consistent with the JSP
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}

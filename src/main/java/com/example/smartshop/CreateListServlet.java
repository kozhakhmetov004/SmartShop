package com.example.smartshop;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

@WebServlet("/createList")
public class CreateListServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String listName = request.getParameter("listName");

        String jdbcURL = "jdbc:mysql://localhost:3306/smartshop";
        String dbUser = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            String sql = "INSERT INTO lists (uuid, owner_id, name, created_at) VALUES (?, ?, ?, NOW())";
            PreparedStatement statement = connection.prepareStatement(sql);

            String uuid = UUID.randomUUID().toString();

            statement.setString(1, uuid);
            statement.setInt(2, userId);
            statement.setString(3, listName);

            statement.executeUpdate();
            statement.close();
            connection.close();

            response.sendRedirect("index.jsp?listId=" + uuid); // redirect to new list
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}

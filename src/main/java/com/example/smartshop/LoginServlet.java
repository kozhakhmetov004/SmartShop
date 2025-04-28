package com.example.smartshop;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String jdbcURL = "jdbc:mysql://localhost:3306/smartshop";
        String dbUser = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            String sql = "SELECT id FROM users WHERE username=? AND password=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                int userId = result.getInt("id");

                HttpSession session = request.getSession();
                session.setAttribute("userId", userId);
                session.setAttribute("username", username);

                response.sendRedirect("index.jsp");
            } else {
                request.setAttribute("errorMessage", "Invalid username or password.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

            result.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}

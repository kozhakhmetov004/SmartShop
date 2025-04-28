package com.example.smartshop;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;

@WebServlet("/toggleDone")
public class ToggleDoneServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int itemId = Integer.parseInt(request.getParameter("itemId"));
        boolean done = request.getParameter("done") != null;

        String jdbcURL = "jdbc:mysql://mysql.railway.internal:3306/railway";
        String dbUser = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            String sql = "UPDATE items SET done = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, done);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();

            stmt.close();
            conn.close();

            response.sendRedirect("index.jsp");  // Redirect back to the index page after updating
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error updating item: " + e.getMessage());
        }
    }
}

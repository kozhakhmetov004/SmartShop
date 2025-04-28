package com.example.smartshop;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;

@WebServlet("/deleteItem")
public class DeleteItemServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int itemId = Integer.parseInt(request.getParameter("itemId"));
        String listUuid = request.getParameter("listUuid"); // <-- get from form

        String jdbcURL = "jdbc:mysql://mysql.railway.internal:3306/railway";
        String dbUser = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            String sql = "DELETE FROM items WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, itemId);
            stmt.executeUpdate();

            stmt.close();
            conn.close();

            response.sendRedirect("index.jsp?list=" + listUuid); // go back to the same list
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error deleting item: " + e.getMessage());
        }
    }
}

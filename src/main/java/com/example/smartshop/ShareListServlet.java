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

@WebServlet("/shareList")
public class ShareListServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String shareUsername = request.getParameter("shareUsername");
        String listUuid = request.getParameter("listUuid");

        String jdbcURL = "jdbc:mysql://mysql.railway.internal:3306/railway";
        String dbUser = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

            // Find list_id
            PreparedStatement findListStmt = conn.prepareStatement("SELECT id FROM lists WHERE uuid = ?");
            findListStmt.setString(1, listUuid);
            ResultSet listRs = findListStmt.executeQuery();
            int listId = -1;
            if (listRs.next()) {
                listId = listRs.getInt("id");
            }
            listRs.close();
            findListStmt.close();

            // Find user_id
            PreparedStatement findUserStmt = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            findUserStmt.setString(1, shareUsername);
            ResultSet userRs = findUserStmt.executeQuery();
            int shareUserId = -1;
            if (userRs.next()) {
                shareUserId = userRs.getInt("id");
            }
            userRs.close();
            findUserStmt.close();

            if (listId != -1 && shareUserId != -1) {
                // Insert permission
                PreparedStatement insertPermissionStmt = conn.prepareStatement(
                        "INSERT INTO list_permissions (list_id, user_id, can_edit) VALUES (?, ?, true)"
                );
                insertPermissionStmt.setInt(1, listId);
                insertPermissionStmt.setInt(2, shareUserId);
                insertPermissionStmt.executeUpdate();
                insertPermissionStmt.close();
            }

            conn.close();
            response.sendRedirect("index.jsp?listId=" + listUuid);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error sharing list: " + e.getMessage());
        }
    }
}

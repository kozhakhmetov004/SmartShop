<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page session="true" %>

<%
    Integer userId = (Integer) session.getAttribute("userId");
    String username = (String) session.getAttribute("username");
    if (userId == null || username == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    String jdbcURL = "jdbc:mysql://localhost:3306/smartshop";
    String dbUser = "root";
    String dbPassword = "";

    String requestedListUuid = request.getParameter("listId");
    String currentListUuid = null;
    int listOwnerId = -1;
    String listName = "";
    boolean hasEditPermission = false;
    List<Map<String, Object>> items = new ArrayList<>();

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword);

        String listSql;
        PreparedStatement listStmt;
        if (requestedListUuid != null) {
            listSql = "SELECT * FROM lists WHERE uuid = ?";
            listStmt = connection.prepareStatement(listSql);
            listStmt.setString(1, requestedListUuid);
        } else {
            listSql = "SELECT * FROM lists WHERE owner_id = ? LIMIT 1";
            listStmt = connection.prepareStatement(listSql);
            listStmt.setInt(1, userId);
        }

        ResultSet listRs = listStmt.executeQuery();
        if (listRs.next()) {
            currentListUuid = listRs.getString("uuid");
            listOwnerId = listRs.getInt("owner_id");
            listName = listRs.getString("name");
        }
        listRs.close();
        listStmt.close();

        if (requestedListUuid != null && currentListUuid == null) {
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>No List Found</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.5/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container text-center mt-5">
    <h2 class="text-danger">No such list exists.</h2>
    <a href="index.jsp" class="btn btn-primary mt-3">Go Back to Home</a>
</div>
</body>
</html>
<%
            return;
        }

        if (currentListUuid != null) {
            String itemSql = "SELECT * FROM items WHERE list_id = (SELECT id FROM lists WHERE uuid = ?)";
            PreparedStatement itemStmt = connection.prepareStatement(itemSql);
            itemStmt.setString(1, currentListUuid);
            ResultSet itemRs = itemStmt.executeQuery();

            while (itemRs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", itemRs.getInt("id"));
                item.put("name", itemRs.getString("name"));
                item.put("quantity", itemRs.getInt("quantity"));
                item.put("done", itemRs.getBoolean("done"));
                items.add(item);
            }
            itemRs.close();
            itemStmt.close();

            String permissionCheckSql = "SELECT can_edit FROM list_permissions WHERE list_id = (SELECT id FROM lists WHERE uuid = ?) AND user_id = ?";
            PreparedStatement permissionStmt = connection.prepareStatement(permissionCheckSql);
            permissionStmt.setString(1, currentListUuid);
            permissionStmt.setInt(2, userId);
            ResultSet permissionRs = permissionStmt.executeQuery();
            if (permissionRs.next()) {
                hasEditPermission = permissionRs.getBoolean("can_edit");
            }
            permissionRs.close();
            permissionStmt.close();
        }

        connection.close();
    } catch (Exception e) {
        e.printStackTrace();
        out.println("Error loading data: " + e.getMessage());
    }

    boolean isOwner = (listOwnerId == userId);
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>SmartShop</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.5/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="text-primary">Welcome, <%= username %>!</h3>
        <form action="logout" method="post">
            <button class="btn btn-outline-danger">Logout</button>
        </form>
    </div>

    <% if (currentListUuid != null) { %>

    <div class="card shadow-sm">
        <div class="card-body text-center">
            <h2 class="card-title mb-3">
                <%= isOwner ? "Your Grocery List" : "Shared List" %>: <span class="text-success"><%= listName %></span>
            </h2>

            <p>Share this list link:</p>
            <div class="alert alert-info">
                <a href="index.jsp?listId=<%= currentListUuid %>">
                    http://localhost:8080/smartshop/index.jsp?listId=<%= currentListUuid %>
                </a>
            </div>

            <% if (isOwner) { %>
            <div class="mb-4">
                <h5>Share this list with others</h5>
                <form action="shareList" method="post" class="d-flex gap-2 justify-content-center">
                    <input type="text" id="shareUsername" name="shareUsername" class="form-control w-25" placeholder="Username" required>
                    <input type="hidden" name="listUuid" value="<%= currentListUuid %>">
                    <button type="submit" class="btn btn-secondary">Share</button>
                </form>
            </div>
            <% } %>

            <ul class="list-group text-start mb-4">
                <% for (Map<String, Object> item : items) { %>
                <li class="list-group-item d-flex justify-content-between align-items-center">
                    <div class="d-flex align-items-center gap-2">
                        <form action="toggleDone" method="post" style="display:inline;">
                            <input type="hidden" name="itemId" value="<%= item.get("id") %>">
                            <input type="checkbox" class="btn-check" id="btn-check-<%=item.get("id")%>" name="done" onchange="this.form.submit()"
                                <%= (Boolean)item.get("done") ? "checked" : "" %>
                                <%= !(isOwner || hasEditPermission) ? "disabled" : "" %> >
                            <label style="width: 50px" class="btn btn-outline-success" for="btn-check-<%=item.get("id")%>">
                                <%= (Boolean)item.get("done") ? "&#x2713;" : "&nbsp;" %>
                            </label>

                        </form>

                        <span class="<%= (Boolean)item.get("done") ? "text-decoration-line-through" : "" %>">
                            <%= item.get("name") %> (x<%= item.get("quantity") %>)
                        </span>
                    </div>
                    <% if (isOwner || hasEditPermission) { %>
                    <form action="deleteItem" method="post" style="display:inline;">
                        <input type="hidden" name="itemId" value="<%= item.get("id") %>">
                        <input type="hidden" name="listUuid" value="<%= currentListUuid %>">
                        <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                    </form>
                    <% } %>
                </li>
                <% } %>
            </ul>

            <% if (isOwner || hasEditPermission) { %>
            <div class="mt-4">
                <h5>Add Item</h5>
                <form action="addItem" method="post" class="row g-2 justify-content-center">
                    <input type="hidden" name="listUuid" value="<%= currentListUuid %>">
                    <div class="col-auto">
                        <input type="text" name="itemName" class="form-control" placeholder="Item name" required>
                    </div>
                    <div class="col-auto">
                        <input type="number" name="quantity" class="form-control" min="1" value="1" required>
                    </div>
                    <div class="col-auto">
                        <button type="submit" class="btn btn-primary">Add Item</button>
                    </div>
                </form>
            </div>
            <% } %>

        </div>
    </div>

    <% } else { %>

    <div class="card shadow-sm text-center p-5">
        <h2>You don't have a grocery list yet.</h2>
        <form action="createList" method="post" class="d-flex justify-content-center align-items-center gap-2 mt-4">
            <input type="text" name="listName" class="form-control w-50" placeholder="List name" required>
            <button type="submit" class="btn btn-success">Create List</button>
        </form>
    </div>
    <% } %>

</div>
</body>
</html>

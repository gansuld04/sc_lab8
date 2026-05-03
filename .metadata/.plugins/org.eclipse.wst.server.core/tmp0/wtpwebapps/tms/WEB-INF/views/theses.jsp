<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="mn.edu.num.tms.core.application.ThesisDTO" %>
<html>
<head>
    <title>NUM Thesis Web Portal</title>
</head>
<body>
    <h2>All Theses</h2>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Student ID</th>
            <th>Supervisor</th>
            <th>Status</th>
        </tr>
        <%
            List<ThesisDTO> thesisList =
                (List<ThesisDTO>) request.getAttribute("thesisList");
            for (ThesisDTO t : thesisList) {
        %>
        <tr>
            <td><%= t.id() %></td>
            <td><%= t.title() %></td>
            <td><%= t.studentId() %></td>
            <td><%= t.supervisorId() != null ? t.supervisorId() : "-" %></td>
            <td><%= t.status() %></td>
        </tr>
        <% } %>
    </table>
</body>
</html>
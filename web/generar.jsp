<%@page import="connection.TableBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.ResultSet"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%@include file='header.html'%>
        <%
            if (session.getAttribute("guardiano") == null) {
                response.sendRedirect("/Guardiano/login.jsp");
            }
        %>
    </head>
    <body>
        <div style="padding: 10px">
            <div class="container">
                <%@include file='menu.html'%>
                <div class="panel panel-default">
                    <div class="panel-heading">Información de acceso a la base de datos</div>
                    <div class="panel-body">
                        <%
                            connection.ConnPostgres c = new connection.ConnPostgres();
                            String tablas[] = request.getParameterValues("tablas[]");
                            if (tablas != null) { %>
                        <ul>
                            <% for (int i = 0; i < tablas.length; i++) {
                                    String partes[] = tablas[i].split(":");
                                    boolean resultado = c.crearAuditoriaPorTabla(partes[0], partes[1]);
                            %>
                            <li><% if (resultado) { %><div class="alert alert-success">Tabla <%=partes[0]%>.<%=partes[1]%> - Auditada</div><%} else {%><div class="alert alert-danger">Tabla <%=partes[0]%>.<%=partes[1]%> - Error</div><%}%> </li>
                                <%}%>
                        </ul>
                        <%}%>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>

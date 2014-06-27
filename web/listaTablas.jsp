<%@page import="system.Config"%>
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
                    <div class="panel-heading">Listado de Tablas</div>
                    <div class="panel-body">
                        <form method="POST" action="generar.jsp" role="form">

                            <table class="table table-striped table-hover">
                                <thead>
                                    <tr>
                                        <th>Esquema</th>
                                        <th>Tabla</th>
                                        <th>Seguimiento</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        if (request.getParameter("host") != null) {
                                            Config.setProperty("host", request.getParameter("host"));
                                        }
                                        if (request.getParameter("origen_usuario") != null) {
                                            Config.setProperty("origen_usuario", request.getParameter("origen_usuario"));
                                        }
                                        if (request.getParameter("origen_clave") != null) {
                                            Config.setProperty("origen_clave", request.getParameter("origen_clave"));
                                        }
                                        if (request.getParameter("origen_base") != null) {
                                            Config.setProperty("origen_base", request.getParameter("origen_base"));
                                        }
                                        if (request.getParameter("audit_usuario") != null) {
                                            Config.setProperty("audit_usuario", request.getParameter("audit_usuario"));
                                        }
                                        if (request.getParameter("audit_clave") != null) {
                                            Config.setProperty("audit_clave", request.getParameter("audit_clave"));
                                        }
                                        if (request.getParameter("audit_base") != null) {
                                            Config.setProperty("audit_base", request.getParameter("audit_base"));
                                        }
                                        connection.ConnPostgres c = new connection.ConnPostgres();
                                        ArrayList<TableBean> rs = c.listarTablas();
                                        if (rs != null) {
                                            for (TableBean fila : rs) {
                                                String a = fila.getSchema();
                                                String b = fila.getTable();
                                    %>
                                    <tr>
                                        <td>
                                            <%= a%>
                                        </td>
                                        <td>
                                            <%= b%>
                                        </td>
                                        <td>
                                            <input type="checkbox" name="tablas[]" value="<%= a%>:<%= b%>" />
                                        </td>
                                    </tr>
                                    <% }%>
                                    <% } else {%>
                                    <tr>
                                        <td colspan="3">
                                            No se pudo conectar a la base de datos o no hay tablas.
                                        </td>
                                    </tr>
                                    <% }%>
                                </tbody>
                            </table>
                            <% if (rs != null) { %>
                            <button class="btn btn-primary">Generar auditoria</button>
                            <% }%>
                        </form>
                    </div>
                    <div class="panel-footer"></div>
                </div>
            </div>
        </div>
    </body>
</html>

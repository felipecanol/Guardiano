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
                    <div class="panel-heading">Resultado de las operaciones</div>
                    <div class="panel-body">
                        <%
                            connection.ConnPostgres c = connection.ConnPostgres.getInstance();
                            String tablas[] = request.getParameterValues("tablas[]");
                            if (tablas != null) { %>
                        <ul>
                            <% for (int i = 0; i < tablas.length; i++) {
                                    String partes[] = tablas[i].split(":");
                                    String triggers[] = request.getParameterValues("trg_" + partes[0] + "_" + partes[1] + "[]");
                                    boolean resultado = c.crearAuditoriaPorTabla(partes[0], partes[1]);
                                    Thread.sleep(10);
                            %>
                            <li>
                                <% if (resultado) {%>
                                <div class="alert alert-success">
                                    Tabla <%=partes[0]%>.<%=partes[1]%> - Auditada
                                </div>
                                <%} else {%>
                                <div class="alert alert-danger">
                                    Tabla <%=partes[0]%>.<%=partes[1]%> - Error
                                </div>
                                <%}%>

                                <% if(triggers !=null){
                                    for (int j = 0; j < triggers.length; j++) {
                                        String trigger[] = triggers[j].split(":");
                                        if (trigger[2].equals("A")) {
                                            if (c.activarTriggers(partes[0], partes[1], trigger[0], true)) {
                                %>
                                <div class="alert alert-success">
                                    <%= trigger[0]%> Activo!
                                </div>
                                <%

                                            } else {

                                %>
                                <div class="alert alert-danger">
                                    <%= trigger[0] + ", error "+c.getError() %>
                                </div>
                                <%          
                                            }
                                        } else {
                                            if (c.activarTriggers(partes[0], partes[1], trigger[0], false)) {

                                %>
                                <div class="alert alert-warning">
                                    <%= trigger[0]%> Desactivado!
                                </div>
                                <%
                                            }else{
                                %>
                                <div class="alert alert-danger">
                                    <%= trigger[0] + ", error "+c.getError() %>
                                </div>
                                <%    
                                            }
                                        }
                                    }
                                }
                                %>
                            </li>
                            <%} c.close();%>
                        </ul>
                        <%}%>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>

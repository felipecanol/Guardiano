<%@page import="system.Config"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%@include file='header.html'%>
        <%
            if(session.getAttribute("guardiano")==null){
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
                        <form role="form" action="listaTablas.jsp">
                            <div class="form-group">
                                <label for="host">Host</label>
                                <input name="host" value="<%= Config.getProperty("host") %>" type="text" class="form-control" id="host" placeholder="Ingrese el host de la base de datos">
                            </div>
                            <div class="panel panel-default">
                                <div class="panel-heading">Información Origen</div>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label for="host">Usuario</label>
                                        <input name="origen_usuario" value="<%= Config.getProperty("origen_usuario") %>" type="text" class="form-control" id="host" placeholder="Ingrese el host de la base de datos">
                                    </div>
                                    <div class="form-group">
                                        <label for="host">Contraseña</label>
                                        <input name="origen_clave" value="<%= Config.getProperty("origen_clave") %>" type="password" class="form-control" id="host" placeholder="Ingrese el host de la base de datos">
                                    </div>
                                    <div class="form-group">
                                        <label for="host">Base de datos</label>
                                        <input name="origen_base" value="<%= Config.getProperty("origen_base") %>" type="text" class="form-control" id="host" placeholder="Ingrese el host de la base de datos">
                                    </div>
                                </div>
                            </div>
                            <div class="panel panel-default">
                                <div class="panel-heading">Información Auditoría</div>
                                <div class="panel-body">
                                    <div class="form-group hidden">
                                        <label for="host">Usuario</label>
                                        <input name="audit_usuario" value="<%= Config.getProperty("audit_usuario") %>" type="text" class="form-control" id="host" placeholder="Ingrese el host de la base de datos">
                                    </div>
                                    <div class="form-group hidden">
                                        <label for="host">Contraseña</label>
                                        <input name="audit_clave" value="<%= Config.getProperty("audit_clave") %>" type="password" class="form-control" id="host" placeholder="Ingrese el host de la base de datos">
                                    </div>
                                    <div class="form-group">
                                        <label for="host">Base de datos</label>
                                        <input name="audit_base" value="<%= Config.getProperty("audit_base") %>" type="text" class="form-control" id="host" placeholder="Ingrese el host de la base de datos">
                                    </div>
                                </div>
                            </div>
                            <button type="submit" class="btn btn-default">Consultar</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>

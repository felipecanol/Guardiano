<%-- 
    Document   : login
    Created on : 25/06/2014, 06:36:31 PM
    Author     : lfcanol
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%@include file='header.html'%>
    </head>
    <body>
        <div style="padding: 50px">
            <div class="container">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <% if (request.getAttribute("msg") != null) { %>
                        <div class="row">
                            <div class="col-lg-11">
                                <div class="alert alert-info"><%= request.getAttribute("msg") %></div>
                            </div>
                        </div>
                        <% }%>
                        <% if (request.getAttribute("err") != null) { %>
                        <div class="row">
                            <div class="col-lg-11">
                                <div class="alert alert-danger"><%= request.getAttribute("err") %></div>
                            </div>
                        </div>
                        <% }%>
                        <form class="form-signin" action="Login" method="POST" role="form">
                            <h2 class="form-signin-heading"><a class="logo2" href="#">Guardiano</a></h2>
                            <input name="u" type="text" class="form-control" placeholder="Usuario" required autofocus>
                            <input name="c" type="password" class="form-control" placeholder="Contraseña" required>
                            <button class="btn btn-lg btn-primary btn-block" type="submit">Ingresar</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>

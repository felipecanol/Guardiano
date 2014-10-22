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
        <script>
            var todos = true;
            $(document).ready(function () {

                $("#cpanel-body").height($(window).height() - 240);
                $(".panel-body-table").height($(window).height() - 320);

                $("#col1").width($("#col21").width());
                $("#col2").width($("#col22").width());
                $("#col3").width($("#col23").width());
                $("#col4").width($("#col24").width());
                $(".activar").click(function () {
                    if ($(this).is(':checked')) {
                        $(this).parents(".etrigger").find(".desactivar").first().prop('checked', false);
                        $(this).parents(".rtabla").find(".accion").first().prop('checked', true);
                    } else {
                        $(this).parents(".etrigger").find(".desactivar").first().prop('checked', true);
                    }
                });
                $(".desactivar").click(function () {
                    if ($(this).is(':checked')) {
                        $(this).parents(".etrigger").find(".activar").first().prop('checked', false);
                        $(this).parents(".rtabla").find(".accion").first().prop('checked', true);
                    } else {
                        $(this).parents(".etrigger").find(".activar").first().prop('checked', true);
                    }
                });

                $("#activar_todos").click(function () {
                    $(".activar").each(function (i, o) {
                        $(o).prop('checked', false);
                        $(o).trigger("click");
                    });
                });
                $("#desactivar_todos").click(function () {
                    $(".desactivar").each(function (i, o) {
                        $(o).prop('checked', false);
                        $(o).trigger("click");
                    });
                });
                $("#auditar_todos").click(function () {
                    $(".accion").each(function (i, o) {
                        $(o).prop('checked', todos);
                    });
                    todos = !todos;
                });
            });
        </script>
        <style>
            .panel-body, .panel-body-table{
                overflow-y: auto;
            }
            .panel-body-table{
                border:1px solid silver;
            }
            #col1,#col2,#col3,#col4{
                vertical-align: middle;
            }
            th{
                text-align: center;
            }
            th a{
                font-size: 10px;
            }
        </style>
    </head>
    <body>
        <div style="padding: 10px">
            <div class="container">
                <%@include file='menu.html'%>
                <form method="POST" action="generar.jsp" role="form">
                    <div class="panel panel-default">
                        <div class="panel-heading">Listado de Tablas</div>
                        <div class="panel-body" id="cpanel-body">
                            <div>
                                <table class="table table-striped table-hover">
                                    <thead>
                                        <tr>
                                            <th id="col1">
                                                Esquema
                                            </th>
                                            <th id="col2">Tabla</th>
                                            <th id="col3">
                                                Triggers<br/>
                                                <a id="activar_todos" href="#">
                                                    Activar
                                                </a>,&nbsp;
                                                <a id="desactivar_todos" href="#">
                                                    Desactivar
                                                </a>
                                            </th>
                                            <th id="col4">
                                                Acción<br/>
                                                <a id="auditar_todos" href="#">
                                                    Todos
                                                </a>
                                            </th>
                                        </tr>
                                    </thead>
                                </table>
                            </div>
                            <div class="panel-body-table">
                                <table class="table table-striped table-hover">
                                    <thead>
                                        <tr>
                                            <th width="80px" id="col21"></th>
                                            <th id="col22"></th>
                                            <th id="col23"></th>
                                            <th width="65px" id="col24"></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            connection.ConnPostgres c = connection.ConnPostgres.getInstance();
                                            c.setConfig(request);
                                            ArrayList<TableBean> rs = c.listarTablas();
                                            if (c.getError().isEmpty()) {
                                                if (rs != null) {
                                                    for (TableBean fila : rs) {
                                                        String a = fila.getSchema();
                                                        String b = fila.getTable();
                                        %>
                                        <tr class="rtabla">
                                            <td>
                                                <%= a%>
                                            </td>
                                            <td>
                                                <%= b%>
                                            </td>
                                            <td>
                                                <%
                                                    for (String trg : fila.getTriggers()) {
                                                        String[] t = trg.split(";");
                                                        String chk = "";
                                                        String chk2 = "checked";
                                                        if (!t[1].equals("D")) {
                                                            chk = "checked";
                                                            chk2 = "";
                                                        }

                                                %>
                                                <div>
                                                    <table class="table">
                                                        <tr>
                                                            <td>Activo</td>
                                                            <td>Inactivo</td>
                                                            <td>Nombre</td>
                                                        </tr>
                                                        <tr class="etrigger">
                                                            <td>
                                                                <input type="checkbox" class="activar" name="trg_<%= a%>_<%= b%>[]" value="<%= t[0] + ":" + t[1]%>:A" <%= chk%> />
                                                            </td>
                                                            <td>
                                                                <input type="checkbox" class="desactivar" name="trg_<%= a%>_<%= b%>[]" value="<%= t[0] + ":" + t[1]%>:I" <%= chk2%> />
                                                            </td>
                                                            <td>
                                                                <%= t[0]%>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </div>
                                                <%
                                                    }
                                                %>
                                            </td>
                                            <td style="vertical-align: middle;text-align: center;">
                                                <input type="checkbox" class="accion" name="tablas[]" value="<%= a%>:<%= b%>" />
                                            </td>
                                        </tr>
                                        <% }%>
                                        <% } else {%>
                                        <tr>
                                            <td colspan="3">
                                                No se pudo conectar a la base de datos o no hay tablas.
                                            </td>
                                        </tr>
                                        <% }
                                        } else {
                                        %>
                                        <tr>
                                            <td colspan="3">
                                                <%= c.getError()%>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                        %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="panel-footer">
                            <% if (rs != null) { %>
                            <input type="submit" class="btn btn-primary" value="Guardar cambios" name="op" />
                            <% }%>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>

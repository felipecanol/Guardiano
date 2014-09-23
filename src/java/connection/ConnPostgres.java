/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author felipe
 */
public class ConnPostgres {

    private String host;
    private String origen_usuario;
    private String origen_clave;
    private String origen_base;
    private String audit_usuario;
    private String audit_clave;
    private String audit_base;
    private String error = "";
    private static final ConnPostgres INSTANCE = new ConnPostgres();

    public void setConfig(HttpServletRequest request) {
        host = request.getParameter("host");
        if (host.isEmpty()) {
            error += "<div>El parametro <b>Host</b> no puede ser vacio</div>";
        }
        origen_usuario = request.getParameter("origen_usuario");
        if (origen_usuario.isEmpty()) {
            error += "<div>El parametro <b>Información Origen Usuario</b> no puede ser vacio</div>";
        }
        origen_clave = request.getParameter("origen_clave");
        if (origen_clave.isEmpty()) {
            error += "<div>El parametro <b>Información Origen Clave</b> no puede ser vacio</div>";
        }
        origen_base = request.getParameter("origen_base");
        if (origen_base.isEmpty()) {
            error += "<div>El parametro <b>Información Origen Base de Datos</b> no puede ser vacio</div>";
        }
        audit_usuario = request.getParameter("audit_usuario");
        if (audit_usuario.isEmpty()) {
            error += "<div>El parametro <b>Información Auditoría Ususario</b> no puede ser vacio</div>";
        }
        audit_clave = request.getParameter("audit_clave");
        if (audit_clave.isEmpty()) {
            error += "<div>El parametro <b>Información Auditoría Clave</b> no puede ser vacio</div>";
        }
        audit_base = request.getParameter("audit_base");
        if (audit_base.isEmpty()) {
            error += "<div>El parametro <b>Información Auditoría Base de Datos</b> no puede ser vacio</div>";
        }
    }

    public static ConnPostgres getInstance() {
        return INSTANCE;
    }

    public Connection connecting() {

        String driver = "org.postgresql.Driver";
        String connectString = "jdbc:postgresql://" + host + "/" + origen_base;
        String user = origen_usuario;
        String password = origen_clave;
        try {
            Class.forName(driver);
            Connection con = DriverManager.getConnection(connectString, user, password);
            return con;
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontro el Driver de conexión");
        } catch (SQLException e) {
            System.err.println("No se pudo conectar a la base de datos.");
        }
        return null;
    }

    @SuppressWarnings("ConvertToTryWithResources")
    public ArrayList<TableBean> listarTablas() {
        try {
            Connection con = this.connecting();
            if (con != null) {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT table_schema,table_name FROM information_schema.tables WHERE table_schema <> 'information_schema' AND table_schema <> 'pg_catalog' ORDER BY table_schema,table_name");
                ArrayList<TableBean> lista = new ArrayList<>();
                while (rs.next()) {
                    TableBean fila = new TableBean();
                    fila.setSchema(rs.getString("table_schema"));
                    fila.setTable(rs.getString("table_name"));
                    ArrayList<String> l = listarTriggers(rs.getString("table_schema"), rs.getString("table_name"));
                    for (String trigger : l) {
                        fila.addTrigger(trigger);
                    }
                    lista.add(fila);
                }
                rs.close();
                stmt.close();
                con.close();
                return lista;
            } else {
                error += "Error en la conexion con la base de datos";
            }
        } catch (SQLException e) {
            error += e.getMessage();
        }
        return null;
        //select * from INFORMATION_SCHEMA.COLUMNS where udt_schema = 'pg_catalog' AND table_name = 'info_req_ruta';
    }

    public ArrayList<String> listarTriggers(String schema, String table) {
        try {
            ArrayList<String> lista = new ArrayList<>();
            Connection con = this.connecting();
            if (con != null) {
                try (Statement stmt = con.createStatement()) {
                    String sql = "SELECT tgname FROM pg_trigger t JOIN pg_class c ON t.tgrelid=c.oid WHERE relname='" + table + "' AND tgname like 'trigger_auditor_" + schema + "%'";
                    try (ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            lista.add(rs.getString("tgname"));
                        }
                        rs.close();
                    }
                    stmt.close();
                }
                con.close();
                return lista;
            } else {
                error += "Error en la conexion con la base de datos";
            }
        } catch (SQLException e) {
            error += e.getMessage();
        }
        return null;
    }

    public void borrarTriggers(String schema, String table) {
        try {
            Connection con = this.connecting();
            if (con != null) {
                ArrayList<String> triggers = listarTriggers(schema, table);
                for (String trigger : triggers) {
                    try (Statement stmt = con.createStatement()) {
                        String sql = "DROP TRIGGER IF EXISTS " + trigger + " ON " + table + ";";
                        stmt.executeQuery(sql);
                        sql = "DROP PROCEDURE IF EXISTS auditor_" + schema + "_" + table + "();";
                        stmt.executeQuery(sql);
                        sql = "DROP PROCEDURE IF EXISTS auditor_" + schema + "_" + table + "_del();";
                        stmt.executeQuery(sql);
                        stmt.close();
                    }
                }
                con.close();
            } else {
                error += "Error en la conexion con la base de datos";
            }
        } catch (SQLException e) {
            error += e.getMessage();
        }
    }

    public boolean crearAuditoriaPorTabla(String schema, String nombre) {
        borrarTriggers(schema, nombre);
        try {
            Connection con = this.connecting();
            String sql;
            if (con != null) {
                try (Statement stmt = con.createStatement()) {
                    int cont = 0;
                    String s1 = "";
                    String s2 = "";
                    if (cont == 0) {
                        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.COLUMNS where table_schema = '" + schema + "' AND table_name = '" + nombre + "';");
                        String conn = "";
                        sql = "CREATE TABLE IF NOT EXISTS " + nombre + " (";
                        sql += "_id serial NOT NULL,";
                        String insert = "insert into " + nombre + " (";
                        String values = " values (";
                        while (rs.next()) {
                            String columnName = rs.getString("column_name");
                            String columnType = rs.getString("data_type").trim();
                            String tmp = rs.getString("character_maximum_length");
                            insert += s1 + columnName;
                            values += s2 + " ' || CASE WHEN NEW." + columnName + " IS NULL THEN 'NULL' ELSE '''' || NEW." + columnName + " || '''' END " + " || ' ";
                            s1 = ",";
                            s2 = ",";
                            int maxCharacter = 0;
                            if (tmp != null) {
                                maxCharacter = Integer.parseInt(rs.getString("character_maximum_length"));
                            }

                            if (columnType.equalsIgnoreCase("character varying") || columnType.equalsIgnoreCase("varchar")) {
                                sql += columnName + " " + columnType + "(" + maxCharacter + "),";
                            } else {
                                sql += columnName + " " + columnType + ",";
                            }
                        }
                        sql += "_accion varchar(50),";
                        sql += "_fecha timestamp DEFAULT current_timestamp );";
                        conn += " select dblink('dbname=" + audit_base + " host=" + host + " user=" + audit_usuario + " password=" + audit_clave + "', '" + sql + "');";

                        insert += ",_accion)";
                        values += ",''' || TG_OP || ''')";
                        String union = insert;
                        System.out.print(conn);
                        stmt.execute(conn);
                        try {
                            stmt.execute("select dblink('dbname=" + audit_base + " host=" + host + " user=" + audit_usuario + " password=" + audit_clave + "', 'ALTER TABLE " + nombre + " ADD CONSTRAINT pk_" + nombre + " PRIMARY KEY(_id);');");
                        } catch (SQLException ex) {
                            System.err.println("Error, pk_" + nombre + " la tabla " + nombre + "ya existe.");
                        }
                        String funcion = "CREATE OR REPLACE FUNCTION auditor_" + schema + "_" + nombre + "() RETURNS TRIGGER AS \n"
                                + "$trigger_audit$\n"
                                + "BEGIN\n"
                                + "    PERFORM dblink('dbname=" + audit_base + " host=" + host + " user=" + audit_usuario + " password=" + audit_clave + "', '" + insert + values + "' );\n"
                                + "    RETURN null;\n"
                                + "END;\n"
                                + "$trigger_audit$ LANGUAGE plpgsql VOLATILE COST 100;";
                        stmt.execute(funcion);

                        funcion = "CREATE OR REPLACE FUNCTION auditor_" + schema + "_" + nombre + "_del() RETURNS TRIGGER AS \n"
                                + "$trigger_audit$\n"
                                + "BEGIN\n"
                                + "    PERFORM dblink('dbname=" + audit_base + " host=" + host + " user=" + audit_usuario + " password=" + audit_clave + "', '" + insert + values + "' );\n"
                                + "    RETURN null;\n"
                                + "END;\n"
                                + "$trigger_audit$ LANGUAGE plpgsql VOLATILE COST 100;";
                        funcion = funcion.replaceAll("NEW.", "OLD.");
                        stmt.execute(funcion);

                        String trigger = "CREATE TRIGGER trigger_auditor_" + schema + "_" + nombre + "\n"
                                + "AFTER INSERT OR UPDATE ON \"" + nombre + "\"\n"
                                + "    FOR EACH ROW EXECUTE PROCEDURE auditor_" + schema + "_" + nombre + "();";
                        stmt.execute(trigger);

                        trigger = "CREATE TRIGGER trigger_auditor_" + schema + "_" + nombre + "_del\n"
                                + "AFTER DELETE ON \"" + nombre + "\"\n"
                                + "    FOR EACH ROW EXECUTE PROCEDURE auditor_" + schema + "_" + nombre + "_del();";
                        stmt.execute(trigger);

                        System.out.println(funcion + trigger);
                        return true;
                    }
                }
                con.close();
            } else {
                System.err.println("Error en la conexion con la base de datos");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    public String getError() {
        return error;
    }
}

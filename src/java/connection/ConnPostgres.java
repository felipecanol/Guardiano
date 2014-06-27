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
import system.Config;

/**
 *
 * @author felipe
 */
public class ConnPostgres {

    public Connection connecting() {
        String driver = "org.postgresql.Driver";
        String connectString = "jdbc:postgresql://" + Config.getProperty("host") + ":5432/" + Config.getProperty("origen_base");
        String user = Config.getProperty("origen_usuario");
        String password = Config.getProperty("origen_clave");
        try {
            Class.forName(driver);
            Connection con = DriverManager.getConnection(connectString, user, password);
            return con;
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontro el Driver de conexión");
        } catch (SQLException e) {
            System.err.println("No se pudo conectar a la base de datos.\n" + e.getMessage());
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
                    lista.add(fila);
                }
                stmt.close();
                con.close();
                return lista;
            } else {
                System.err.println("Error en la conexion con la base de datos");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public boolean crearAuditoriaPorTabla(String schema, String nombre) {
        try {
            Connection con = this.connecting();
            if (con != null) {
                try (Statement stmt = con.createStatement()) {
                    int cont = 0;
                    String s1 = "";
                    String s2 = "";
                    if (cont == 0) {
                        ResultSet rs = stmt.executeQuery("select * from INFORMATION_SCHEMA.COLUMNS where table_schema = '" + schema + "' AND table_name = '" + nombre + "';");
                        String conn = "";
                        String sql = "CREATE TABLE " + nombre + " (";
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
                        conn += " select dblink('dbname=" + Config.getProperty("audit_base") + " host=" + Config.getProperty("host") + " user=" + Config.getProperty("audit_usuario") + " password=" + Config.getProperty("audit_clave") + "', '" + sql + "');";
                        
                        insert += ",_accion)";
                        values += ",''' || TG_OP || ''')";
                        String union = insert;
                        System.out.print(sql);
                        stmt.execute(conn);
                        
                        stmt.execute("select dblink('dbname=" + Config.getProperty("audit_base") + " host=" + Config.getProperty("host") + " user=" + Config.getProperty("audit_usuario") + " password=" + Config.getProperty("audit_clave") + "', 'ALTER TABLE " + nombre + " ADD CONSTRAINT pk_" + nombre + " PRIMARY KEY(_id);');");
                        
                        String funcion = "CREATE OR REPLACE FUNCTION auditor_" + schema + "_" + nombre + "() RETURNS TRIGGER AS \n"
                                + "$trigger_audit$\n"
                                + "BEGIN\n"
                                + "    PERFORM dblink('dbname=" + Config.getProperty("audit_base") + " host=" + Config.getProperty("host") + " user=" + Config.getProperty("audit_usuario") + " password=" + Config.getProperty("audit_clave") + "', '" + insert + values + "' );\n"
                                + "    RETURN null;\n"
                                + "END;\n"
                                + "$trigger_audit$ LANGUAGE plpgsql VOLATILE COST 100;";
                        stmt.execute(funcion);
                        
                        String trigger = "CREATE TRIGGER trigger_auditor_" + schema + "_" + nombre + "\n"
                                + "AFTER INSERT OR UPDATE OR DELETE ON \"" + nombre + "\"\n"
                                + "    FOR EACH ROW EXECUTE PROCEDURE auditor_" + schema + "_" + nombre + "();";
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
}
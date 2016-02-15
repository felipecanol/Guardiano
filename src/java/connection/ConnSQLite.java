/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author felipe
 */
public class ConnSQLite {

    //    private static final String s = "\\"; // For Windows
    private static final String s = "/"; // For Unix
    private Connection c = null;
    private Statement stmt = null;
    public ResultSet rs = null;

    public void connecting() {
        if (c == null) {
            try {
                InputStream in = getClass().getClassLoader().getResourceAsStream(s + "system" + s + "store.sqlite");
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:" + in);
                c.setAutoCommit(false);
            } catch (ClassNotFoundException | SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    public void close() {
        if (c != null) {
            try {
                c.close();
                c = null;
            } catch (SQLException ex) {
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
                stmt = null;
            } catch (SQLException ex) {
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            }
        }
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException ex) {
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
            }
        }
    }

    public ResultSet query(String sql) {
        connecting();
        if (c != null) {
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery(sql);
                return rs;
            } catch (SQLException ex) {
                Logger.getLogger(ConnSQLite.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    public void update(String sql) {
        connecting();
        if (c != null) {
            try {
                stmt = c.createStatement();
                stmt.executeUpdate(sql);
            } catch (SQLException ex) {
                Logger.getLogger(ConnSQLite.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

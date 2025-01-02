package database;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class Database_con {

    // Class-level fields to store active connections, statements, and result sets
    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    
    // DataSource for JNDI lookup
    private DataSource dataSource;

    // Constructor to initialize the DataSource
    public Database_con() {
        try {
            InitialContext ctx = new InitialContext();
            // Replace "java:/comp/env/jdbc/MyDataSource" with your JNDI name
            dataSource = (DataSource) ctx.lookup("java:/comp/env/jdbc/MyDataSource");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Method to establish a connection using JNDI DataSource
    public Connection connectDb() {
        try {
            if (dataSource != null) {
                con = dataSource.getConnection();
                if (con != null) {
                    System.out.println("Connection established");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    // Method to execute a query and retrieve data
    public ResultSet executeQuery(String query) {
        try {
            if (con == null || con.isClosed()) {
                connectDb();  // Connect if not already connected
            }
            if (con != null) {
                stmt = con.createStatement();
                rs = stmt.executeQuery(query);
                return rs;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void commit() {
        try {
            if (con != null && !con.isClosed()) {
                con.commit();
                System.out.println("Transaction committed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rollback() {
        try {
            if (con != null && !con.isClosed()) {
                con.rollback();
                System.out.println("Transaction rolled back");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to close all active resources
    public void closeResources() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null && !con.isClosed()) {
                con.close();
            }
            System.out.println("All resources closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

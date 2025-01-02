package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import database.Database_con;

public class FetchEmployee {

    public static void main(String[] args) {
        // Instantiate the database connection class
        Database_con db = new Database_con();
        
        // Establish the connection
        
        System.out.println("Before Connecting to database:");
        Connection con = db.connectDb();
        System.out.println("after Connecting to database:");
        
        // Query to fetch employee data
        String query = "SELECT * FROM employee";
        
        try {
            // Execute the query and retrieve the result
            ResultSet rs = db.executeQuery(query);

            // Process the result set and print employee details
            System.out.println("Employee Details:");
            while (rs.next()) {
                int id = rs.getInt("e_id");
                String name = rs.getString("e_name");
                String department = rs.getString("e_dept");
                String designation = rs.getString("e_desg");

                // Print each employee's details
                System.out.println("ID: " + id + ", Name: " + name + ", "
                		+ "Department: " + department + ", Designation: " + designation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            db.closeResources();
        }
    }
}

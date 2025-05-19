package DB;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 * Utility class to test and verify database connection for MySQL/MariaDB
 */
public class DatabaseConnectionTester {
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cine";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Default empty password for XAMPP
    
    /**
     * Tests the database connection and verifies the required tables exist
     * @return true if connection is successful and tables exist
     */
    public static boolean testConnection() {
        // Step 1: Try to connect to the MySQL/MariaDB server
        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL/MariaDB JDBC Driver loaded successfully!");
            
            // Connect to the server (not yet selecting a database)
            try (Connection serverConn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD)) {
                
                System.out.println("Connection to MySQL/MariaDB server established!");
                
                
                // Ensure the 'cine' database exists
                try (Statement stmt = serverConn.createStatement()) {
                    stmt.execute("CREATE DATABASE IF NOT EXISTS cine");
                    System.out.println("Database 'cine' exists or was created successfully!");
                }
            }
            
            // Connect directly to the 'cine' database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                System.out.println("Database connection established successfully!");
                
                // Check if the 'tickets' table exists
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(
                             "SELECT COUNT(*) FROM information_schema.tables " +
                             "WHERE table_schema = 'cine' AND table_name = 'tickets'")) {
                    
                    if (rs.next() && rs.getInt(1) == 0) {
                        // Table doesn't exist – initialize DB and retry
                        System.out.println("Tickets table does not exist - creating it now");
                        DBManager.initDatabase();
                        return testConnection(); // Retry after creation
                    } else {
                        System.out.println("Tickets table exists!");
                        // Optionally query number of records
                        try (ResultSet testRs = stmt.executeQuery("SELECT COUNT(*) FROM tickets")) {
                            if (testRs.next()) {
                                int count = testRs.getInt(1);
                                System.out.println("Current ticket count: " + count);
                            }
                        }
                    }
                }
                
                return true;
            }
        } catch (ClassNotFoundException e) {
             // Driver class not found
            System.err.println("MySQL/MariaDB JDBC Driver not found!");
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            // General connection or query failure
            System.err.println("Database connection failed!");
            e.printStackTrace();
            
            // If the database is missing, try to create it
            if (e.getMessage().contains("Unknown database") || 
                e.getMessage().contains("doesn't exist")) {
                System.out.println("Database doesn't exist - attempting to create it");
                try {
                    // Try to connect to the server without database
                    Connection serverConn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
                    Statement stmt = serverConn.createStatement();
                    stmt.execute("CREATE DATABASE IF NOT EXISTS cine");
                    serverConn.close();
                    
                    DBManager.initDatabase();
                    return testConnection(); // Retry after creation
                } catch (SQLException initEx) {
                    System.err.println("Failed to create database: " + initEx.getMessage());
                    initEx.printStackTrace();
                }
            }
            
            return false;
        }
    }
    
    /**
     * Tests the connection and shows the result in a dialog box (Swing UI)
     */
    public static void verifyAndShowResult() {
        // Call the testConnection method to check DB availability
        boolean connected = testConnection();
        
        if (connected) {
            // If the connection is successful, show a confirmation dialog
            JOptionPane.showMessageDialog(null, 
                "MySQL/MariaDB connection successful!\nThe cine database is properly connected.", 
                "Database Connection", 
                JOptionPane.INFORMATION_MESSAGE); // Information icon and title
        } else {
            // If the connection fails, show an error dialog with possible causes
            JOptionPane.showMessageDialog(null, 
                "MySQL/MariaDB connection failed!\n" +
                "Please ensure:\n" +
                "1. XAMPP is running and MySQL/MariaDB service is started\n" +
                "2. Username/password are correct (default: root/empty)\n" +
                "3. MySQL Connector/J is in your classpath", 
                "Database Connection Error", 
                JOptionPane.ERROR_MESSAGE);  // Error icon and title
        }
    }
}
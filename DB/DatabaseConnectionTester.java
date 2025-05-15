package DB;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 * Utility class to test and verify database connection for MySQL/MariaDB
 */
public class DatabaseConnectionTester {
    // MySQL/MariaDB connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cine";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Password vacío para XAMPP por defecto
    
    /**
     * Tests the database connection and verifies the required tables exist
     * @return true if connection is successful and tables exist
     */
    public static boolean testConnection() {
        // Step 1: Try to connect to the MySQL/MariaDB server
        try {
            // Load MySQL/MariaDB JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL/MariaDB JDBC Driver loaded successfully!");
            
            // First check if we can connect to the MySQL/MariaDB server
            try (Connection serverConn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD)) {
                
                System.out.println("Connection to MySQL/MariaDB server established!");
                
                // Step 2: Check if database exists, create if it doesn't
                try (Statement stmt = serverConn.createStatement()) {
                    stmt.execute("CREATE DATABASE IF NOT EXISTS cine");
                    System.out.println("Database 'cine' exists or was created successfully!");
                }
            }
            
            // Step 3: Now connect to the specific database
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                System.out.println("Database connection established successfully!");
                
                // Step 4: Verify tickets table exists
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(
                             "SELECT COUNT(*) FROM information_schema.tables " +
                             "WHERE table_schema = 'cine' AND table_name = 'tickets'")) {
                    
                    if (rs.next() && rs.getInt(1) == 0) {
                        System.out.println("Tickets table does not exist - creating it now");
                        DBManager.initDatabase();
                        return testConnection(); // Recursively test again
                    } else {
                        System.out.println("Tickets table exists!");
                        // Test a simple query
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
            System.err.println("MySQL/MariaDB JDBC Driver not found!");
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
            
            // Check for common issues
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
                    return testConnection(); // Try again after initialization
                } catch (SQLException initEx) {
                    System.err.println("Failed to create database: " + initEx.getMessage());
                    initEx.printStackTrace();
                }
            }
            
            return false;
        }
    }
    
    /**
     * Tests connection and displays the result in a dialog
     */
    public static void verifyAndShowResult() {
        boolean connected = testConnection();
        
        if (connected) {
            JOptionPane.showMessageDialog(null, 
                "MySQL/MariaDB connection successful!\nThe cine database is properly connected.", 
                "Database Connection", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, 
                "MySQL/MariaDB connection failed!\n" +
                "Please ensure:\n" +
                "1. XAMPP is running and MySQL/MariaDB service is started\n" +
                "2. Username/password are correct (default: root/empty)\n" +
                "3. MySQL Connector/J is in your classpath", 
                "Database Connection Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
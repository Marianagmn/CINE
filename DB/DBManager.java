package DB;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class manages the connection and operations with the MySQL/MariaDB
 * database.
 */
public class DBManager {
    // Database configuration (for XAMPP default)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cine";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Password vacío para XAMPP por defecto

    /**
     * Initializes the database by creating the schema and tables if they don't
     * exist.
     */
    public static void initDatabase() {
        // First, create the database if it doesn't exist
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
                Statement stmt = conn.createStatement()) {

            // Now connect to the database and create tables
            stmt.execute("CREATE DATABASE IF NOT EXISTS cine");
            System.out.println("Base de datos creada o ya existente");

        } catch (SQLException e) {
            System.err.println("Error al crear la base de datos: " + e.getMessage());
            e.printStackTrace();
            return; // Exit if we cannot connect to the server
        }

        // SQL statement to create the 'tickets' table if it doesn't already exist
        // The table stores ticket information for movie sessions including seat,
        // status, and reservation time
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // Crear tabla de tickets (adaptada para MySQL/MariaDB)
            String ticketsTableSQL = "CREATE TABLE IF NOT EXISTS tickets (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," + // Unique identifier for each ticket
                    "movie VARCHAR(100)," + // Movie name
                    "time VARCHAR(20)," + // Session time
                    "theater VARCHAR(100)," + // Theater name
                    "city VARCHAR(50)," + // City where the theater is located
                    "seat VARCHAR(10)," + // Seat identifier
                    "combo VARCHAR(100)," + // Combo or snack package associated with the ticket
                    "total INT," + // Total price of the ticket
                    "status VARCHAR(20)," + // Status of the ticket: 'reserved' or 'purchased'
                    "reservation_time DATETIME," + // Timestamp of when the seat was reserved
                    "INDEX idx_ticket_unique (movie, time, seat, status)" + // Index to optimize searches for seat
                                                                            // availability
                    ")";
            stmt.execute(ticketsTableSQL);

            System.out.println("Base de datos inicializada correctamente");
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Establishes and returns a connection to the database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Reserves a seat for a movie session, if available.
    public static boolean reserveSeat(String movie, String time, String theater, String city,
            String seat, String combo, int total) {
        // Check if the requested seat is available for reservation
        if (!isSeatAvailable(movie, time, seat)) {
            System.out.println("El asiento " + seat + " no está disponible para " + movie + " a las " + time);
            return false; // Exit early if seat is already reserved or purchased
        }
        // SQL statement to insert a new ticket reservation with status 'reserved'
        String sql = "INSERT INTO tickets (movie, time, theater, city, seat, combo, total, status, reservation_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Get current date and time as reservation timestamp formatted for SQL DATETIME
            String reservationTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Set the parameters for the prepared statement
            pstmt.setString(1, movie);
            pstmt.setString(2, time);
            pstmt.setString(3, theater);
            pstmt.setString(4, city);
            pstmt.setString(5, seat);
            pstmt.setString(6, combo);
            pstmt.setInt(7, total);
            pstmt.setString(8, "reserved"); // Estado inicial de reserva
            pstmt.setString(9, reservationTime);

            // Execute the insert query
            int result = pstmt.executeUpdate();
            System.out.println("Asiento " + seat + " reservado para " + movie + " - Resultado: " + result);

            // Return true if the insert affected at least one row
            return result > 0;
        } catch (SQLException e) {

            // Log and print error details if reservation fails
            System.err.println("Error al reservar asiento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean confirmPurchase(String movie, String time, String seat) {
        String sql = "UPDATE tickets SET status = 'purchased' " +
                "WHERE movie = ? AND time = ? AND seat = ? AND status = 'reserved'";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameters for the prepared statement to prevent SQL injection
            pstmt.setString(1, movie);
            pstmt.setString(2, time);
            pstmt.setString(3, seat);

            // Execute the update and check how many rows were affected
            int result = pstmt.executeUpdate();
            System.out.println("Confirmando compra para " + seat + " en " + movie + " - Resultado: " + result);

            // Return true if at least one row was updated, meaning purchase confirmed
            return result > 0;
        } catch (SQLException e) {
            // Log error if any SQL exception occurs during update
            System.err.println("Error al confirmar la compra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isSeatAvailable(String movie, String time, String seat) {
        // SQL query to count how many tickets exist for the given movie, time, and seat
        // where the status is either 'reserved' or 'purchased' (meaning the seat is
        // taken)
        String sql = "SELECT COUNT(*) FROM tickets " +
                "WHERE movie = ? AND time = ? AND seat = ? AND status IN ('reserved', 'purchased')";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set parameter
            pstmt.setString(1, movie);
            pstmt.setString(2, time);
            pstmt.setString(3, seat);

            // Execute the query and get the result set
            ResultSet rs = pstmt.executeQuery();

            // Move to the first result row and check the count value
            // If count is 0, it means the seat is not reserved or purchased, so it is
            // available
            boolean isAvailable = rs.next() && rs.getInt(1) == 0;
            // Print the availability status for debugging purposes
            System.out.println(
                    "Verificando disponibilidad de " + seat + " para " + movie + " a las " + time + ": " + isAvailable);
            // Return true if seat is available, false otherwise
            return isAvailable;
        } catch (SQLException e) {
            // Print error message if there is an issue querying the database
            System.err.println("Error al verificar disponibilidad: " + e.getMessage());
            e.printStackTrace();
            // Return false in case of error (consider seat unavailable)
            return false;
        }
    }

    public static void cleanupExpiredReservations() {
        String sql = "DELETE FROM tickets " +
                "WHERE status = 'reserved' AND " +
                "DATE_ADD(reservation_time, INTERVAL 15 MINUTE) < NOW()";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            int affectedRows = stmt.executeUpdate(sql);

            if (affectedRows > 0) {
                System.out.println("Se eliminaron " + affectedRows + " reservas expiradas");
            }

        } catch (SQLException e) {
            System.err.println("Error al limpiar reservas expiradas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ResultSet getAllPurchasedTickets() {
        try {
            cleanupExpiredReservations(); // Clean up expired reservations before fetching purchased tickets

            // Establish a connection to the database
            Connection conn = getConnection();

            // Create a statement object to execute the query
            Statement stmt = conn.createStatement();

            // Execute the query to select all tickets with status 'purchased' and return
            // the result set
            return stmt.executeQuery("SELECT * FROM tickets WHERE status = 'purchased'");
        } catch (SQLException e) {

            // Print an error message if there is a problem retrieving purchased tickets
            System.err.println("Error al obtener tickets comprados: " + e.getMessage());
            e.printStackTrace();
            // Return null if an exception occurs
            return null;
        }
    }

    // Method to clear tickets for a specific function
    public static void clearTicketsForFunction(String movie, String time) {
        String sql = "DELETE FROM tickets WHERE movie = ? AND time = ? AND status = 'reserved'";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, movie);
            pstmt.setString(2, time);

            int affectedRows = pstmt.executeUpdate();
            System.out.println("Se eliminaron " + affectedRows + " reservas para " + movie + " a las " + time);
        } catch (SQLException e) {
            System.err.println("Error al limpiar tickets para la función: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // New method to save a ticket (called from GUI)
    public static boolean saveTicket(String movie, String time, String theater,
            String city, String seat, String combo, int total) {
        // First, attempt to reserve the seat
        boolean reserved = reserveSeat(movie, time, theater, city, seat, combo, total);

        // If reservation fails, show an error (though this should be handled in GUI)
        if (!reserved) {
            System.err.println("Error al guardar ticket: No se pudo reservar el asiento " + seat);
            return false;
        }
        return true;
    }

    // Method to get all reserved and purchased tickets
    public static ResultSet getAllTickets() {
        try {
            cleanupExpiredReservations(); // Clean up any expired reservations first

            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tickets WHERE status IN ('reserved', 'purchased')");
            System.out.println("Obteniendo todos los tickets (reservados y comprados)");
            return rs;
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los tickets: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
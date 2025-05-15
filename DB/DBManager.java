package DB;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DBManager {
    // Configuración de la conexión a MySQL/MariaDB
    private static final String DB_URL = "jdbc:mysql://localhost:3306/cine";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Password vacío para XAMPP por defecto
    
    // Método para inicializar la base de datos
    public static void initDatabase() {
        // Primero intentamos crear la base de datos si no existe
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Crear la base de datos si no existe
            stmt.execute("CREATE DATABASE IF NOT EXISTS cine");
            System.out.println("Base de datos creada o ya existente");
            
        } catch (SQLException e) {
            System.err.println("Error al crear la base de datos: " + e.getMessage());
            e.printStackTrace();
            return; // Salir si no podemos conectar al servidor MySQL/MariaDB
        }
        
        // Ahora conectamos a la base de datos y creamos las tablas
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Crear tabla de tickets (adaptada para MySQL/MariaDB)
            String ticketsTableSQL = "CREATE TABLE IF NOT EXISTS tickets (" +
                         "id INT PRIMARY KEY AUTO_INCREMENT," +
                         "movie VARCHAR(100)," +
                         "time VARCHAR(20)," +
                         "theater VARCHAR(100)," +
                         "city VARCHAR(50)," +
                         "seat VARCHAR(10)," +
                         "combo VARCHAR(100)," +
                         "total INT," +
                         "status VARCHAR(20)," +  // 'reserved' or 'purchased'
                         "reservation_time DATETIME," +
                         "INDEX idx_ticket_unique (movie, time, seat, status)" +
                         ")";
            stmt.execute(ticketsTableSQL);
            
            System.out.println("Base de datos inicializada correctamente");
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Método para obtener una conexión a la base de datos
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static boolean reserveSeat(String movie, String time, String theater, String city, 
                                       String seat, String combo, int total) {
        // Primero, verificar si el asiento está disponible
        if (!isSeatAvailable(movie, time, seat)) {
            System.out.println("El asiento " + seat + " no está disponible para " + movie + " a las " + time);
            return false;
        }

        String sql = "INSERT INTO tickets (movie, time, theater, city, seat, combo, total, status, reservation_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Obtener la hora actual para la reserva
            String reservationTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            pstmt.setString(1, movie);
            pstmt.setString(2, time);
            pstmt.setString(3, theater);
            pstmt.setString(4, city);
            pstmt.setString(5, seat);
            pstmt.setString(6, combo);
            pstmt.setInt(7, total);
            pstmt.setString(8, "reserved");  // Estado inicial de reserva
            pstmt.setString(9, reservationTime);
            
            int result = pstmt.executeUpdate();
            System.out.println("Asiento " + seat + " reservado para " + movie + " - Resultado: " + result);
            return result > 0;
        } catch (SQLException e) {
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
            
            pstmt.setString(1, movie);
            pstmt.setString(2, time);
            pstmt.setString(3, seat);
            
            int result = pstmt.executeUpdate();
            System.out.println("Confirmando compra para " + seat + " en " + movie + " - Resultado: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error al confirmar la compra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isSeatAvailable(String movie, String time, String seat) {
        String sql = "SELECT COUNT(*) FROM tickets " +
                     "WHERE movie = ? AND time = ? AND seat = ? AND status IN ('reserved', 'purchased')";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, movie);
            pstmt.setString(2, time);
            pstmt.setString(3, seat);
            
            ResultSet rs = pstmt.executeQuery();
            boolean isAvailable = rs.next() && rs.getInt(1) == 0;
            System.out.println("Verificando disponibilidad de " + seat + " para " + movie + " a las " + time + ": " + isAvailable);
            return isAvailable;
        } catch (SQLException e) {
            System.err.println("Error al verificar disponibilidad: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void cleanupExpiredReservations() {
        // Eliminar reservas con más de 15 minutos de antigüedad (adaptado para MySQL/MariaDB)
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
            cleanupExpiredReservations();  // Limpiar reservas expiradas antes de mostrar
            
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            return stmt.executeQuery("SELECT * FROM tickets WHERE status = 'purchased'");
        } catch (SQLException e) {
            System.err.println("Error al obtener tickets comprados: " + e.getMessage());
            e.printStackTrace();
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
            cleanupExpiredReservations();  // Clean up any expired reservations first
            
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
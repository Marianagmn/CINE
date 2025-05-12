package DB;

import java.sql.*;

public class DBManager {
    private static final String DB_URL = "jdbc:sqlite:cine.db";

    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS tickets (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "movie TEXT," +
                         "time TEXT," +
                         "theater TEXT," +
                         "city TEXT," +
                         "seat TEXT," +
                         "combo TEXT," +
                         "total INTEGER" +
                         ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveTicket(String movie, String time, String theater, String city, String seat, String combo, int total) {
        String sql = "INSERT INTO tickets (movie, time, theater, city, seat, combo, total) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movie);
            pstmt.setString(2, time);
            pstmt.setString(3, theater);
            pstmt.setString(4, city);
            pstmt.setString(5, seat);
            pstmt.setString(6, combo);
            pstmt.setInt(7, total);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getAllTickets() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            return stmt.executeQuery("SELECT * FROM tickets");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
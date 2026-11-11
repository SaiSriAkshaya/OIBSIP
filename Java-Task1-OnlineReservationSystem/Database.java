import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

/** All JDBC / SQLite access lives here. Every query uses PreparedStatement (prevents SQL injection). */
public class Database {
    private static final String URL = "jdbc:sqlite:reservation.db";
    private static final Random RANDOM = new Random();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /** Creates tables and inserts demo data on first run. */
    public static void init() throws SQLException {
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "username TEXT PRIMARY KEY, password_hash TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS trains ("
                    + "train_no INTEGER PRIMARY KEY, train_name TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS reservations ("
                    + "pnr INTEGER PRIMARY KEY, passenger_name TEXT NOT NULL, "
                    + "train_no INTEGER NOT NULL, train_name TEXT NOT NULL, "
                    + "class_type TEXT NOT NULL, journey_date TEXT NOT NULL, "
                    + "source TEXT NOT NULL, destination TEXT NOT NULL)");

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT OR IGNORE INTO users (username, password_hash) VALUES (?, ?)")) {
                ps.setString(1, "admin");
                ps.setString(2, hash("admin123"));
                ps.executeUpdate();
            }

            Object[][] trains = {
                {12723, "Telangana Express"},
                {12760, "Charminar Express"},
                {12951, "Mumbai Rajdhani"},
                {12627, "Karnataka Express"},
                {12301, "Howrah Rajdhani"},
                {12009, "Shatabdi Express"}
            };
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT OR IGNORE INTO trains (train_no, train_name) VALUES (?, ?)")) {
                for (Object[] t : trains) {
                    ps.setInt(1, (Integer) t[0]);
                    ps.setString(2, (String) t[1]);
                    ps.executeUpdate();
                }
            }
        }
    }

    public static boolean validateLogin(String username, String password) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password_hash = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash(password));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Returns the train name, or null if the train number does not exist. */
    public static String findTrainName(int trainNo) throws SQLException {
        String sql = "SELECT train_name FROM trains WHERE train_no = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, trainNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    /** Saves the booking, generates a unique 10-digit PNR and returns it. */
    public static long saveReservation(Reservation r) throws SQLException {
        try (Connection c = getConnection()) {
            long pnr = generateUniquePnr(c);
            String sql = "INSERT INTO reservations (pnr, passenger_name, train_no, train_name, "
                    + "class_type, journey_date, source, destination) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, pnr);
                ps.setString(2, r.getPassengerName());
                ps.setInt(3, r.getTrainNo());
                ps.setString(4, r.getTrainName());
                ps.setString(5, r.getClassType());
                ps.setString(6, r.getJourneyDate());
                ps.setString(7, r.getSource());
                ps.setString(8, r.getDestination());
                ps.executeUpdate();
            }
            r.setPnr(pnr);
            return pnr;
        }
    }

    public static Reservation findReservation(long pnr) throws SQLException {
        String sql = "SELECT passenger_name, train_no, train_name, class_type, journey_date, "
                + "source, destination FROM reservations WHERE pnr = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, pnr);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Reservation(pnr, rs.getString(1), rs.getInt(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7));
            }
        }
    }

    public static boolean deleteReservation(long pnr) throws SQLException {
        String sql = "DELETE FROM reservations WHERE pnr = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, pnr);
            return ps.executeUpdate() > 0;
        }
    }

    private static long generateUniquePnr(Connection c) throws SQLException {
        while (true) {
            long pnr = 1000000000L + (long) (RANDOM.nextDouble() * 9000000000L);
            try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM reservations WHERE pnr = ?")) {
                ps.setLong(1, pnr);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return pnr;
                }
            }
        }
    }

    private static String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

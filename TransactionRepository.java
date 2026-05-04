import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void save(Transaction t) {
        String sql = "INSERT INTO transactions (booking_id, amount, type, method, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, t.getBookingId());
            pstmt.setDouble(2, t.getAmount());
            pstmt.setString(3, t.getType());
            pstmt.setString(4, t.getMethod());
            pstmt.setString(5, t.getTimestamp().format(dtf));
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public double getTotalRevenue() {
        String sql = "SELECT SUM(CASE WHEN type='PAYMENT' THEN amount ELSE -amount END) AS total FROM transactions";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public List<Transaction> findByBookingId(String bookingId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE booking_id = ? ORDER BY timestamp";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String bid = rs.getString("booking_id");
        double amount = rs.getDouble("amount");
        String type = rs.getString("type");
        String method = rs.getString("method");
        LocalDateTime ts = LocalDateTime.parse(rs.getString("timestamp"), dtf);
        return new Transaction(id, bid, amount, type, method, ts);
    }
}
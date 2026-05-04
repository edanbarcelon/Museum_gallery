import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void save(Reservation res) {
        String sql = "INSERT INTO reservations (booking_id, guest_name, contact_number, visit_datetime, pax, status, exit_time, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, res.getBookingId());
            pstmt.setString(2, res.getGuestName());
            pstmt.setString(3, res.getContactNumber());
            pstmt.setString(4, res.getVisitDateTime().format(dtf));
            pstmt.setInt(5, res.getPax());
            pstmt.setString(6, res.getStatus());
            pstmt.setString(7, res.getExitTime() != null ? res.getExitTime().format(dtf) : null);
            pstmt.setString(8, res.getPaymentStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Reservation findById(String bookingId) {
        String sql = "SELECT * FROM reservations WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Reservation> findByName(String guestName) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE guest_name LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + guestName + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Update by deleting old row and inserting updated one (same booking_id)
    public void update(Reservation res) {
        delete(res.getBookingId());
        save(res);
    }

    public void cancel(String bookingId) {
        Reservation old = findById(bookingId);
        if (old != null) {
            Reservation cancelled = old.withStatus("Cancelled").withPaymentStatus("Refunded");
            update(cancelled);
        }
    }

    public void delete(String bookingId) {
        String sql = "DELETE FROM reservations WHERE booking_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookingId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public int getLastBookingNumber() {
        String sql = "SELECT booking_id FROM reservations ORDER BY booking_id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString("booking_id");
                return Integer.parseInt(lastId.substring(lastId.indexOf('_') + 1));
            }
        } catch (SQLException e) {}
        return 0;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        String bookingId = rs.getString("booking_id");
        String guestName = rs.getString("guest_name");
        String contact = rs.getString("contact_number");
        String visitStr = rs.getString("visit_datetime");
        LocalDateTime visit = visitStr != null ? LocalDateTime.parse(visitStr, dtf) : null;
        int pax = rs.getInt("pax");
        String status = rs.getString("status");
        String exitStr = rs.getString("exit_time");
        LocalDateTime exit = exitStr != null ? LocalDateTime.parse(exitStr, dtf) : null;
        String paymentStatus = rs.getString("payment_status");

        return new Reservation.Builder()
                .bookingId(bookingId)
                .guestName(guestName)
                .contactNumber(contact)
                .visitDateTime(visit)
                .pax(pax)
                .status(status)
                .exitTime(exit)
                .paymentStatus(paymentStatus != null ? paymentStatus : "Pending")
                .build();
    }
}
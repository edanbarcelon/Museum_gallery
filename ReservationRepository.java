import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void save(Reservation res) {
        String sql = "INSERT INTO reservations (booking_id, guest_name, contact_number, visit_datetime, pax, status, exit_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, res.getBookingId());
            pstmt.setString(2, res.getGuestName());
            pstmt.setString(3, res.getContactNumber());
            pstmt.setString(4, res.getVisitDateTime().format(dtf));
            pstmt.setInt(5, res.getPax());
            pstmt.setString(6, res.getStatus());
            pstmt.setString(7, res.getExitTime() != null ? res.getExitTime().format(dtf) : null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Reservation findById(String bookingId) {
        String sql = "SELECT * FROM reservations WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToReservation(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Reservation> findByName(String guestName) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE guest_name LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + guestName + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(mapRowToReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Reservation res) {
        String sql = "UPDATE reservations SET guest_name=?, contact_number=?, visit_datetime=?, pax=?, status=?, exit_time=? WHERE booking_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, res.getGuestName());
            pstmt.setString(2, res.getContactNumber());
            pstmt.setString(3, res.getVisitDateTime().format(dtf));
            pstmt.setInt(4, res.getPax());
            pstmt.setString(5, res.getStatus());
            pstmt.setString(6, res.getExitTime() != null ? res.getExitTime().format(dtf) : null);
            pstmt.setString(7, res.getBookingId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancel(String bookingId) {
        String sql = "UPDATE reservations SET status='Cancelled' WHERE booking_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookingId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String bookingId) {
        String sql = "DELETE FROM reservations WHERE booking_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bookingId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLastBookingNumber() {
        String sql = "SELECT booking_id FROM reservations ORDER BY booking_id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastId = rs.getString("booking_id");
                // Expected format: MG2026_01
                String numPart = lastId.substring(lastId.indexOf('_') + 1);
                return Integer.parseInt(numPart);
            }
        } catch (SQLException e) {
            // Table may be empty
        }
        return 0;
    }

    private Reservation mapRowToReservation(ResultSet rs) throws SQLException {
        String bookingId = rs.getString("booking_id");
        String guestName = rs.getString("guest_name");
        String contact = rs.getString("contact_number");
        String visitStr = rs.getString("visit_datetime");
        LocalDateTime visit = visitStr != null ? LocalDateTime.parse(visitStr, dtf) : null;
        int pax = rs.getInt("pax");
        String status = rs.getString("status");
        String exitStr = rs.getString("exit_time");
        LocalDateTime exitTime = exitStr != null ? LocalDateTime.parse(exitStr, dtf) : null;

        return new Reservation.Builder()
                .bookingId(bookingId)
                .guestName(guestName)
                .contactNumber(contact)
                .visitDateTime(visit)
                .pax(pax)
                .status(status)
                .exitTime(exitTime)
                .build();
    }
}
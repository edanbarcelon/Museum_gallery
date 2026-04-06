import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeSlotDAO {
    private static final int MAX_CAPACITY_PER_SLOT = 50;
    private static final DateTimeFormatter slotFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00");

    public static int getRemainingCapacity(LocalDateTime dateTime) {
        String slotKey = dateTime.format(slotFormatter);
        String sql = "SELECT used_pax FROM slot_capacity WHERE slot_key = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, slotKey);
            ResultSet rs = pstmt.executeQuery();
            int used = rs.next() ? rs.getInt("used_pax") : 0;
            return MAX_CAPACITY_PER_SLOT - used;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean reserveSlot(LocalDateTime dateTime, int pax) {
        String slotKey = dateTime.format(slotFormatter);
        int remaining = getRemainingCapacity(dateTime);
        if (pax > remaining) return false;

        String sql = "INSERT INTO slot_capacity (slot_key, used_pax) VALUES (?, ?) " +
                     "ON CONFLICT(slot_key) DO UPDATE SET used_pax = used_pax + ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, slotKey);
            pstmt.setInt(2, pax);
            pstmt.setInt(3, pax);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void releaseSlot(LocalDateTime dateTime, int pax) {
        String slotKey = dateTime.format(slotFormatter);
        String sql = "UPDATE slot_capacity SET used_pax = used_pax - ? WHERE slot_key = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pax);
            pstmt.setString(2, slotKey);
            pstmt.executeUpdate();
            // Clean up zero or negative entries
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM slot_capacity WHERE used_pax <= 0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean updateSlot(LocalDateTime oldDateTime, LocalDateTime newDateTime, int oldPax, int newPax) {
        if (!oldDateTime.equals(newDateTime)) {
            releaseSlot(oldDateTime, oldPax);
            return reserveSlot(newDateTime, newPax);
        } else {
            int diff = newPax - oldPax;
            if (diff > 0) {
                int remaining = getRemainingCapacity(newDateTime);
                if (diff <= remaining) {
                    String slotKey = newDateTime.format(slotFormatter);
                    String sql = "UPDATE slot_capacity SET used_pax = used_pax + ? WHERE slot_key = ?";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, diff);
                        pstmt.setString(2, slotKey);
                        pstmt.executeUpdate();
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                } else return false;
            } else if (diff < 0) {
                releaseSlot(newDateTime, -diff);
                return true;
            }
            return true;
        }
    }
}
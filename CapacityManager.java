import java.sql.*; 
  
public class CapacityManager { 
    private static final int MAX_OCCUPANCY = 200;  // Changed from 150 
  
    public static boolean canEnter(int pax) { 
        return (getCurrentInside() + pax) <= MAX_OCCUPANCY; 
    } 
  
    public static void addVisitors(int pax) { 
        updateOccupancy(pax); 
    } 
  
    public static void removeVisitors(int pax) { 
        updateOccupancy(-pax); 
    } 
  
    private static synchronized void updateOccupancy(int delta) { 
        String sql = "UPDATE current_occupancy SET current_inside = current_inside + ? WHERE id = 1"; 
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) { 
            pstmt.setInt(1, delta); 
            pstmt.executeUpdate(); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } 
    } 
  
    public static int getCurrentInside() { 
        String sql = "SELECT current_inside FROM current_occupancy WHERE id = 1"; 
        try (Connection conn = DatabaseConnection.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) { 
            if (rs.next()) return rs.getInt("current_inside"); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } 
        return 0; 
    } 
  
    public static int getMaxOccupancy() { return MAX_OCCUPANCY; } 
  
    public static double getOccupancyPercentage() { 
        return (getCurrentInside() * 100.0) / MAX_OCCUPANCY; 
    } 
  
    public static boolean isFull() { 
        return getCurrentInside() >= MAX_OCCUPANCY; 
    } 
}
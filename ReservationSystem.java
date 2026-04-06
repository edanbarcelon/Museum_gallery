import java.time.LocalDate; 
import java.time.LocalTime; 
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter; 
import java.time.format.DateTimeParseException; 
import java.util.List; 
import java.util.Scanner; 
  
public class ReservationSystem { 
    private static Scanner scanner = new Scanner(System.in); 
    private static ReservationRepository repository = new ReservationRepository(); 
    private static int bookingCounter = repository.getLastBookingNumber() + 1; 
  
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy"); 
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm"); 
  
    private static String generateBookingId() { 
        return "MG2026_" + String.format("%02d", bookingCounter++); 
    } 
  
    private static boolean showBackOption(String prompt) { 
        System.out.println("\n[1] " + prompt); 
        System.out.println("[2] Back to Main Menu"); 
        System.out.print("Enter your choice: "); 
        String choice = scanner.nextLine().trim(); 
        return choice.equals("1"); 
    } 
  
    // Feature 1: Register Reservation 
    public static void registerReservation() { 
        while (true) { 
            System.out.println("\n# REGISTER RESERVATION"); 
            System.out.print("Enter Guest Name: "); 
            String name = scanner.nextLine().trim(); 
            System.out.print("Enter Contact Number: "); 
            String contact = scanner.nextLine().trim(); 
            System.out.print("Enter Date of Visit (MM/DD/YYYY): "); 
            String dateStr = scanner.nextLine().trim(); 
            System.out.print("Enter Time of Visit (HH:MM): "); 
            String timeStr = scanner.nextLine().trim(); 
            System.out.print("Pax Count: "); 
            String paxStr = scanner.nextLine().trim(); 
  
            LocalDate visitDate; 
            LocalTime visitTime; 
            int pax; 
            try { 
                visitDate = LocalDate.parse(dateStr, dateFormatter); 
                visitTime = LocalTime.parse(timeStr, timeFormatter); 
                pax = Integer.parseInt(paxStr); 
            } catch (DateTimeParseException | NumberFormatException e) { 
                System.out.println("Invalid input. Please try again."); 
                if (!showBackOption("Try Again")) return; 
                continue; 
            } 
  
            LocalDateTime visitDateTime = LocalDateTime.of(visitDate, visitTime); 
  
            if (name.isEmpty() || contact.isEmpty()) { 
                System.out.println("Reservation failed. Mandatory fields are incomplete."); 
                if (!showBackOption("Try Again")) return; 
                continue; 
            } 
  
            int remaining = TimeSlotDAO.getRemainingCapacity(visitDateTime); 
            if (pax > remaining) { 
                System.out.println("Reservation failed. The selected time/date slot is at full capacity."); 
                System.out.println("Please choose another slot."); 
                if (!showBackOption("Try Again")) return; 
                continue; 
            } 
  
            System.out.println("\n[1] Submit"); 
            System.out.println("[2] Back to Main Menu"); 
            System.out.print("Enter your choice: "); 
            String submitChoice = scanner.nextLine().trim(); 
            if (!submitChoice.equals("1")) return; 
  
            if (TimeSlotDAO.reserveSlot(visitDateTime, pax)) { 
                String bookingId = generateBookingId(); 
                Reservation res = new Reservation.Builder() 
                        .bookingId(bookingId) 
                        .guestName(name) 
                        .contactNumber(contact) 
                        .visitDateTime(visitDateTime) 
                        .pax(pax) 
                        .build(); 
                repository.save(res); 
                System.out.println("\nReservation successful!"); 
                System.out.println("Booking ID: " + bookingId + " has been generated for " + name + "."); 
            } else { 
                System.out.println("Reservation failed. Capacity issue."); 
            } 
            System.out.println("\nPress Enter to continue..."); 
            scanner.nextLine(); 
            return; 
        } 
    } 
  
    // Feature 2: Search Guests 
    public static void searchGuests() { 
        while (true) { 
            System.out.println("\n# SEARCH GUESTS"); 
            System.out.print("Enter Booking ID or Guest Name: "); 
            String query = scanner.nextLine().trim(); 
            System.out.println("\n[1] Search"); 
            System.out.println("[2] Back to Main Menu"); 
            System.out.print("Enter your choice: "); 
            String choice = scanner.nextLine().trim(); 
            if (!choice.equals("1")) return; 
  
            if (query.isEmpty()) { 
                System.out.println("No input provided."); 
                continue; 
            } 
  
            Reservation res = repository.findById(query); 
            if (res != null) { 
                System.out.println("\nGuest record found. Booking ID: " + res.getBookingId() + " - " + res.getGuestName() + "."); 
                System.out.println(res); 
            } else { 
                List<Reservation> list = repository.findByName(query); 
                if (!list.isEmpty()) { 
                    for (Reservation r : list) { 
                        System.out.println("\nFound: " + r.getBookingId() + " - " + r.getGuestName()); 
                        System.out.println(r); 
                    } 
                } else { 
                    System.out.println("\nGuest record not found. Please check the Booking ID or guest name."); 
                } 
            } 
            System.out.println("\nPress Enter to continue..."); 
            scanner.nextLine(); 
            return; 
        } 
    } 
  
    // Feature 3: Update Information 
    public static void updateInformation() { 
        while (true) { 
            System.out.println("\n# UPDATE INFORMATION"); 
            System.out.print("Enter Booking ID or Guest Name: "); 
            String query = scanner.nextLine().trim(); 
            System.out.println("\n[1] Search Record"); 
            System.out.println("[2] Back to Main Menu"); 
            System.out.print("Enter your choice: "); 
            String searchChoice = scanner.nextLine().trim(); 
            if (!searchChoice.equals("1")) return; 
  
            Reservation res = repository.findById(query); 
            if (res == null) { 
                List<Reservation> list = repository.findByName(query); 
                if (list.isEmpty()) { 
                    System.out.println("No record found."); 
                    continue; 
                } 
                if (list.size() > 1) { 
                    System.out.println("Multiple records found. Please use Booking ID for exact match."); 
                    continue; 
                } 
                res = list.get(0); 
            } 
  
            if (!res.getStatus().equals("Pending")) { 
                System.out.println("Update failed. Reservation cannot be modified after check-in or cancellation."); 
                System.out.println("\nPress Enter to continue..."); 
                scanner.nextLine(); 
                return; 
            } 
  
            System.out.println("\nUpdate Details"); 
            System.out.print("Enter Guest Name [" + res.getGuestName() + "]: "); 
            String newName = scanner.nextLine().trim(); 
            if (!newName.isEmpty()) res.setGuestName(newName); 
  
            System.out.print("Enter Contact Number [" + res.getContactNumber() + "]: "); 
            String newContact = scanner.nextLine().trim(); 
            if (!newContact.isEmpty()) res.setContactNumber(newContact); 
  
            System.out.print("Enter Date of Visit (MM/DD/YYYY) [" + res.getVisitDateTime().format(dateFormatter) + "]: "); 
            String dateStr = scanner.nextLine().trim(); 
            System.out.print("Enter Time of Visit (HH:MM) [" + res.getVisitDateTime().format(timeFormatter) + "]: "); 
            String timeStr = scanner.nextLine().trim(); 
            LocalDateTime newDateTime = res.getVisitDateTime(); 
            if (!dateStr.isEmpty() && !timeStr.isEmpty()) { 
                try { 
                    LocalDate newDate = LocalDate.parse(dateStr, dateFormatter); 
                    LocalTime newTime = LocalTime.parse(timeStr, timeFormatter); 
                    newDateTime = LocalDateTime.of(newDate, newTime); 
                } catch (DateTimeParseException e) { 
                    System.out.println("Invalid date/time format. Keeping original."); 
                } 
            } 
  
            System.out.print("Pax Count [" + res.getPax() + "]: "); 
            String paxStr = scanner.nextLine().trim(); 
            int newPax = res.getPax(); 
            if (!paxStr.isEmpty()) { 
                try { 
                    newPax = Integer.parseInt(paxStr); 
                } catch (NumberFormatException e) { 
                    System.out.println("Invalid Pax number. Keeping original."); 
                } 
            } 
  
            System.out.println("\n[1] Save Changes"); 
            System.out.println("[2] Cancel"); 
            System.out.print("Enter your choice: "); 
            String saveChoice = scanner.nextLine().trim(); 
            if (!saveChoice.equals("1")) return; 
  
            boolean capacityOk = TimeSlotDAO.updateSlot(res.getVisitDateTime(), newDateTime, res.getPax(), newPax); 
            if (!capacityOk) { 
                System.out.println("Update failed. The updated Pax exceeds capacity for the selected time slot."); 
                System.out.println("\nPress Enter to continue..."); 
                scanner.nextLine(); 
                return; 
            } 
  
            res.setVisitDateTime(newDateTime); 
            res.setPax(newPax); 
            repository.update(res); 
            System.out.println("\nReservation information successfully updated."); 
            System.out.println("\nPress Enter to continue..."); 
            scanner.nextLine(); 
            return; 
        } 
    } 
  
    // Feature 4: Confirm Arrival 
    public static void confirmArrival() { 
        while (true) { 
            System.out.println("\n# CONFIRM ARRIVAL"); 
            System.out.print("Enter Booking ID: "); 
            String bookingId = scanner.nextLine().trim(); 
            System.out.println("\n[1] Confirm Entry"); 
            System.out.println("[2] Back to Main Menu"); 
            System.out.print("Enter your choice: "); 
            String choice = scanner.nextLine().trim(); 
            if (!choice.equals("1")) return; 
  
            Reservation res = repository.findById(bookingId); 
            if (res == null || res.getStatus().equals("Cancelled")) { 
                System.out.println("\nEntry denied. Booking ID not found or ticket already used."); 
            } else if (!res.getStatus().equals("Pending")) { 
                System.out.println("\nEntry denied. Ticket has already been used."); 
            } else if (!CapacityManager.canEnter(res.getPax())) { 
                System.out.println("\nMaximum capacity reached. Please wait."); 
            } else { 
                res.setStatus("CheckedIn"); 
                CapacityManager.addVisitors(res.getPax()); 
                repository.update(res); 
                System.out.println("\nEntry confirmed."); 
                System.out.println("Welcome and Enjoy the Museum & Gallery!"); 
            } 
            System.out.println("\nPress Enter to continue..."); 
            scanner.nextLine(); 
            return; 
        } 
    } 
  
    // Feature 5: Record Departure 
    public static void recordDeparture() { 
        while (true) { 
            System.out.println("\n# RECORD DEPARTURE"); 
            System.out.print("Enter Booking ID: "); 
            String bookingId = scanner.nextLine().trim(); 
            System.out.println("\n[1] Confirm Check-out"); 
            System.out.println("[2] Back to Main Menu"); 
            System.out.print("Enter your choice: "); 
            String choice = scanner.nextLine().trim(); 
            if (!choice.equals("1")) return; 
  
            Reservation res = repository.findById(bookingId); 
            if (res == null) { 
                System.out.println("\nNo Record Found. The Booking ID did not match any active reservation."); 
            } else if (!res.getStatus().equals("CheckedIn")) { 
                System.out.println("\nDeparture cannot be recorded. The guest has not arrived yet."); 
            } else if (res.getStatus().equals("Left")) { 
                System.out.println("\nInvalid Action. The guest has already left."); 
            } else { 
                res.setStatus("Left"); 
                res.setExitTime(LocalDateTime.now()); 
                CapacityManager.removeVisitors(res.getPax()); 
                repository.update(res); 
                System.out.println("\nGuests departed successfully. [" + bookingId + "] has been checked out."); 
            } 
            System.out.println("\nPress Enter to continue..."); 
            scanner.nextLine(); 
            return; 
        } 
    } 
  
    // Feature 6: Monitor Capacity 
    public static void monitorCapacity() { 
        while (true) { 
            int inside = CapacityManager.getCurrentInside(); 
            int max = CapacityManager.getMaxOccupancy(); 
            double percent = CapacityManager.getOccupancyPercentage(); 
            System.out.println("\n# MONITOR CAPACITY"); 
            System.out.println("Maximum Capacity: " + max + " Visitors"); 
            System.out.println("Current Visitors Inside: " + inside); 
            System.out.printf("Occupancy: %.0f%%\n", percent); 
            System.out.print("Status: "); 
            if (CapacityManager.isFull()) { 
                System.out.println("FULL"); 
                System.out.println("Entry not allowed."); 
            } else { 
                System.out.println("NOT FULL"); 
                System.out.println("Entry allowed."); 
            } 
            System.out.println("\n[1] Refresh"); 
            System.out.println("[2] Back to Main Menu"); 
            System.out.print("Enter your choice: "); 
            String choice = scanner.nextLine().trim(); 
            if (!choice.equals("1")) return; 
        } 
    } 
  
    // Feature 7: Cancel Ticket 
    public static void cancelTicket() { 
        while (true) { 
            System.out.println("\n# CANCEL TICKET"); 
            System.out.print("Enter Ticket ID: "); 
            String bookingId = scanner.nextLine().trim(); 
            System.out.println("\n[1] Request Cancellation"); 
            System.out.println("[2] Back to Main Menu"); 
            System.out.print("Enter your choice: "); 
            String choice = scanner.nextLine().trim(); 
            if (!choice.equals("1")) return; 
  
            Reservation res = repository.findById(bookingId); 
            if (res == null || !res.getStatus().equals("Pending")) { 
                System.out.println("\nCancellation failed. Ticket not eligible for refund or invalid ticket ID."); 
            } else { 
                TimeSlotDAO.releaseSlot(res.getVisitDateTime(), res.getPax()); 
                repository.cancel(bookingId); 
                System.out.println("\nYour ticket has been successfully canceled."); 
                System.out.println("Refund will be processed shortly."); 
            } 
            System.out.println("\nPress Enter to continue..."); 
            scanner.nextLine(); 
            return; 
        } 
    } 
} 

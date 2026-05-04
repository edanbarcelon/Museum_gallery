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
    private static TransactionRepository transactionRepo = new TransactionRepository();
    private static int bookingCounter = repository.getLastBookingNumber() + 1;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter dtfDisplay = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

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

    private static boolean canCheckInNow(Reservation res) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime visitStart = res.getVisitDateTime();
        // Allow check‑in up to 1 hour after the reserved time
        LocalDateTime visitEnd = visitStart.plusHours(1);
        return now.isAfter(visitStart) && now.isBefore(visitEnd);
    }
    
    // ========== SEARCH GUESTS ==========
    public static void searchGuests() {
        while (true) {
            System.out.println("\n# SEARCH GUESTS");
            System.out.print("Enter Booking ID or Guest Name: ");
            String query = scanner.nextLine().trim();
            System.out.println("\n[1] Search");
            System.out.println("[2] Back to Main Menu");
            System.out.print("Enter your choice: ");
            if (!scanner.nextLine().trim().equals("1")) return;

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

    // ========== CONFIRM ARRIVAL (with time validation and occupancy update) ==========
    public static void confirmArrival() {
        while (true) {
            System.out.println("\n# CONFIRM ARRIVAL");
            System.out.print("Enter Booking ID: ");
            String bookingId = scanner.nextLine().trim();
            System.out.println("\n[1] Confirm Entry");
            System.out.println("[2] Back to Main Menu");
            System.out.print("Enter your choice: ");
            if (!scanner.nextLine().trim().equals("1")) return;

            Reservation res = repository.findById(bookingId);
            if (res == null || res.getStatus().equals("Cancelled")) {
                System.out.println("\nEntry denied. Booking ID not found or ticket already used.");
            } else if (!res.getStatus().equals("Pending")) {
                System.out.println("\nEntry denied. Ticket has already been used.");
            } else if (!canCheckInNow(res)) {
                System.out.println("\nEntry denied. You can only check in during your reserved time slot.");
                System.out.println("Your reserved time: " + res.getVisitDateTime().format(dtfDisplay));
            } else if (!CapacityManager.canEnter(res.getPax())) {
                System.out.println("\nMaximum capacity reached. Please wait.");
                System.out.println("Current occupancy: " + CapacityManager.getCurrentInside() + "/" + CapacityManager.getMaxOccupancy());
            } else {
                // Update status
                res.setStatus("CheckedIn");
                // Add visitors to occupancy (updates database)
                CapacityManager.addVisitors(res.getPax());
                // Save reservation status change
                repository.update(res);
                System.out.println("\nEntry confirmed.");
                System.out.println("Welcome and Enjoy the Museum & Gallery!");
                System.out.println("Current visitors inside: " + CapacityManager.getCurrentInside());
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
    }

    // ========== RECORD DEPARTURE ==========
    public static void recordDeparture() {
        while (true) {
            System.out.println("\n# RECORD DEPARTURE");
            System.out.print("Enter Booking ID: ");
            String bookingId = scanner.nextLine().trim();
            System.out.println("\n[1] Confirm Check-out");
            System.out.println("[2] Back to Main Menu");
            System.out.print("Enter your choice: ");
            if (!scanner.nextLine().trim().equals("1")) return;

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
                System.out.println("Current visitors inside: " + CapacityManager.getCurrentInside());
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
    }
    // ========== CANCEL TICKET (with refund) ==========
    public static void cancelTicket() {
        while (true) {
            System.out.println("\n# CANCEL TICKET");
            System.out.print("Enter Ticket ID: ");
            String bookingId = scanner.nextLine().trim();
            System.out.println("\n[1] Request Cancellation");
            System.out.println("[2] Back to Main Menu");
            System.out.print("Enter your choice: ");
            if (!scanner.nextLine().trim().equals("1")) return;

            Reservation res = repository.findById(bookingId);
            if (res == null || !res.getStatus().equals("Pending")) {
                System.out.println("\nCancellation failed. Ticket not eligible for refund or invalid ticket ID.");
            } else {
                if ("Paid".equals(res.getPaymentStatus())) {
                    List<Transaction> transactions = transactionRepo.findByBookingId(bookingId);
                    double totalPaid = transactions.stream()
                            .filter(t -> "PAYMENT".equals(t.getType()))
                            .mapToDouble(Transaction::getAmount).sum();
                    if (totalPaid > 0) {
                        Transaction refund = new Transaction(0, bookingId, totalPaid, "REFUND", "CASH", LocalDateTime.now());
                        transactionRepo.save(refund);
                        System.out.printf("Refund of ₱%.2f processed.\n", totalPaid);
                    }
                }
                TimeSlotDAO.releaseSlot(res.getVisitDateTime(), res.getPax());
                repository.cancel(bookingId);
                res.setPaymentStatus("Refunded");
                repository.update(res);
                System.out.println("\nYour ticket has been successfully canceled.");
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
    }
}
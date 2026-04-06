import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n# MUSEUM & GALLERY FRONT DESK SYSTEM");
            System.out.println("Welcome to the Museum & Gallery System\n");
            System.out.println("[1] Register Reservation");
            System.out.println("[2] Search Guests");
            System.out.println("[3] Update Information");
            System.out.println("[4] Confirm Arrival");
            System.out.println("[5] Record Departure");
            System.out.println("[6] Monitor Capacity");
            System.out.println("[7] Cancel Ticket");
            System.out.println("[8] Exit");
            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": ReservationSystem.registerReservation(); break;
                case "2": ReservationSystem.searchGuests(); break;
                case "3": ReservationSystem.updateInformation(); break;
                case "4": ReservationSystem.confirmArrival(); break;
                case "5": ReservationSystem.recordDeparture(); break;
                case "6": ReservationSystem.monitorCapacity(); break;
                case "7": ReservationSystem.cancelTicket(); break;
                case "8": System.out.println("Exiting system. Goodbye!"); return;
                default: System.out.println("Invalid option. Try again.");
            }
        }
    }
}
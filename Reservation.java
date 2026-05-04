import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

<<<<<<< HEAD
public class Reservation {
    private String bookingId;
    private String guestName;
    private String contactNumber;
    private LocalDateTime visitDateTime;
    private int pax;
    private String status;
    private LocalDateTime exitTime;
    private String paymentStatus;
=======
public final class Reservation {
    private final String bookingId;
    private final String guestName;
    private final String contactNumber;
    private final LocalDateTime visitDateTime;
    private final int pax;
    private final String status;
    private final LocalDateTime exitTime;
12

>>>>>>> origin/main
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

    private Reservation(Builder builder) {
        this.bookingId = builder.bookingId;
        this.guestName = builder.guestName;
        this.contactNumber = builder.contactNumber;
        this.visitDateTime = builder.visitDateTime;
        this.pax = builder.pax;
        this.status = builder.status;
        this.exitTime = builder.exitTime;
<<<<<<< HEAD
        this.paymentStatus = builder.paymentStatus;
=======
24
>>>>>>> origin/main
    }

    // Only getters – no setters (immutable)
    public String getBookingId() { return bookingId; }
    public String getGuestName() { return guestName; }
    public String getContactNumber() { return contactNumber; }
    public LocalDateTime getVisitDateTime() { return visitDateTime; }
    public int getPax() { return pax; }
    public String getStatus() { return status; }
<<<<<<< HEAD
    public LocalDateTime getExitTime() { return exitTime; }
    public String getPaymentStatus() { return paymentStatus; }

    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setVisitDateTime(LocalDateTime visitDateTime) { this.visitDateTime = visitDateTime; }
    public void setPax(int pax) { this.pax = pax; }
    public void setStatus(String status) { this.status = status; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
=======
34
    public String getPaymentStatus() { return paymentStatus; }

    // Create a new Reservation with a different status (used for check‑in, cancel, etc.)
    public Reservation withStatus(String newStatus) {
        return new Builder()
                .bookingId(this.bookingId)
                .guestName(this.guestName)
                .contactNumber(this.contactNumber)
                .visitDateTime(this.visitDateTime)
                .pax(this.pax)
                .status(newStatus)
                .exitTime(this.exitTime)
47
                .build();
    }

    public Reservation withExitTime(LocalDateTime exitTime) {
        return new Builder()
                .bookingId(this.bookingId)
                .guestName(this.guestName)
                .contactNumber(this.contactNumber)
                .visitDateTime(this.visitDateTime)
                .pax(this.pax)
                .status(this.status)
                .exitTime(exitTime)
                .paymentStatus(this.paymentStatus)
                .build();
    }

    public Reservation withPaymentStatus(String newPaymentStatus) {
        return new Builder()
                .bookingId(this.bookingId)
                .guestName(this.guestName)
                .contactNumber(this.contactNumber)
                .visitDateTime(this.visitDateTime)
                .pax(this.pax)
                .status(this.status)
                .exitTime(this.exitTime)
73
                .build();
    }

    // For updates that change guestName, contact, date, pax – create completely new
    public Reservation withUpdatedDetails(String guestName, String contactNumber, LocalDateTime visitDateTime, int pax) {
        return new Builder()
                .bookingId(this.bookingId)
                .guestName(guestName)
                .contactNumber(contactNumber)
                .visitDateTime(visitDateTime)
                .pax(pax)
                .status(this.status)
                .exitTime(this.exitTime)
87
                .build();
    }
>>>>>>> origin/main

    @Override
    public String toString() {
        return "Booking ID: " + bookingId +
               "\nGuest: " + guestName +
               "\nContact: " + contactNumber +
               "\nVisit: " + (visitDateTime != null ? visitDateTime.format(formatter) : "N/A") +
               "\nPax: " + pax +
<<<<<<< HEAD
               "\nStatus: " + status;
               "\nPayment: " + paymentStatus;
=======
               "\nStatus: " + status +
99
>>>>>>> origin/main
    }

    // Builder – declared static final to prevent subclassing
    public static final class Builder {
        private String bookingId;
        private String guestName;
        private String contactNumber;
        private LocalDateTime visitDateTime;
        private int pax;
        private String status = "Pending";
        private LocalDateTime exitTime = null;
        private String paymentStatus = "Pending";

<<<<<<< HEAD
        public Builder bookingId(String bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder guestName(String guestName) {
            this.guestName = guestName;
            return this;
        }

        public Builder contactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
            return this;
        }

        public Builder visitDateTime(LocalDateTime visitDateTime) {
            this.visitDateTime = visitDateTime;
            return this;
        }

        public Builder pax(int pax) {
            this.pax = pax;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder exitTime(LocalDateTime exitTime) {
            this.exitTime = exitTime;
            return this;
        }
        public Builder paymentStatus(String paymentStatus) {   
            this.paymentStatus = paymentStatus;
            return this; }
=======
        public Builder bookingId(String bookingId) { this.bookingId = bookingId; return this; }
        public Builder guestName(String guestName) { this.guestName = guestName; return this; }
        public Builder contactNumber(String contactNumber) { this.contactNumber = contactNumber; return this; }
        public Builder visitDateTime(LocalDateTime visitDateTime) { this.visitDateTime = visitDateTime; return this; }
        public Builder pax(int pax) { this.pax = pax; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder exitTime(LocalDateTime exitTime) { this.exitTime = exitTime; return this; }
        public Builder paymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; return this; }
>>>>>>> origin/main

        public Reservation build() {
            if (bookingId == null || bookingId.isEmpty())
                throw new IllegalArgumentException("Booking ID required");
            if (guestName == null || guestName.isEmpty())
                throw new IllegalArgumentException("Guest name required");
            if (contactNumber == null || contactNumber.isEmpty())
                throw new IllegalArgumentException("Contact number required");
            if (visitDateTime == null)
                throw new IllegalArgumentException("Visit date/time required");
            if (pax <= 0)
                throw new IllegalArgumentException("Pax must be > 0");
            return new Reservation(this);
        }
    }
}
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reservation {
    private String bookingId;
    private String guestName;
    private String contactNumber;
    private LocalDateTime visitDateTime;
    private int pax;
    private String status;
    private LocalDateTime exitTime;
    private String paymentStatus;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

    // Private constructor – only Builder can create instances
    private Reservation(Builder builder) {
        this.bookingId = builder.bookingId;
        this.guestName = builder.guestName;
        this.contactNumber = builder.contactNumber;
        this.visitDateTime = builder.visitDateTime;
        this.pax = builder.pax;
        this.status = builder.status;
        this.exitTime = builder.exitTime;
        this.paymentStatus = builder.paymentStatus;
    }

    // Getters and setters (all mutable for simplicity)
    public String getBookingId() { return bookingId; }
    public String getGuestName() { return guestName; }
    public String getContactNumber() { return contactNumber; }
    public LocalDateTime getVisitDateTime() { return visitDateTime; }
    public int getPax() { return pax; }
    public String getStatus() { return status; }
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

    @Override
    public String toString() {
        return "Booking ID: " + bookingId +
               "\nGuest: " + guestName +
               "\nContact: " + contactNumber +
               "\nVisit: " + (visitDateTime != null ? visitDateTime.format(formatter) : "N/A") +
               "\nPax: " + pax +
               "\nStatus: " + status;
               "\nPayment: " + paymentStatus;
    }

    // Builder class
    public static class Builder {
        private String bookingId;
        private String guestName;
        private String contactNumber;
        private LocalDateTime visitDateTime;
        private int pax;
        private String status = "Pending";
        private LocalDateTime exitTime = null;
        private String paymentStatus = "Pending";

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

        public Reservation build() {
            if (bookingId == null || bookingId.isEmpty()) {
                throw new IllegalStateException("Booking ID cannot be null or empty");
            }
            if (guestName == null || guestName.isEmpty()) {
                throw new IllegalStateException("Guest name cannot be null or empty");
            }
            if (contactNumber == null || contactNumber.isEmpty()) {
                throw new IllegalStateException("Contact number cannot be null or empty");
            }
            if (visitDateTime == null) {
                throw new IllegalStateException("Visit date/time cannot be null");
            }
            if (pax <= 0) {
                throw new IllegalStateException("Pax must be greater than zero");
            }
            return new Reservation(this);
        }
    }
}
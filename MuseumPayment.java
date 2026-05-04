public class MuseumPayment extends PaymentFramework {
    private int regularPax;
    private int discountedPax;

    public MuseumPayment(int regularPax, int discountedPax) {
        super(100.0, regularPax + discountedPax);
        this.regularPax = regularPax;
        this.discountedPax = discountedPax;
    }

    @Override
    protected double calculateSubtotal() {
        double regularTotal = regularPax * baseTicketPrice;
        double discountedTotal = discountedPax * baseTicketPrice * (1 - DISCOUNT_RATE);
        return regularTotal + discountedTotal;
    }

    @Override
    protected double applyDiscount(double amount) {
        return amount;
    }

    @Override
    public void processInvoice(double total, double payment) {
        double regularTotal = regularPax * baseTicketPrice;
        double discountedTotal = discountedPax * baseTicketPrice * (1 - DISCOUNT_RATE);
        double discountAmount = discountedPax * baseTicketPrice * DISCOUNT_RATE;

        System.out.println("\n--- MUSEUM INVOICE ---");
        System.out.printf("Regular visitors: %d x ₱%.2f = ₱%.2f\n", regularPax, baseTicketPrice, regularTotal);
        System.out.printf("Discounted visitors: %d x ₱%.2f = ₱%.2f (20%% off)\n", discountedPax, baseTicketPrice, discountedTotal);
        System.out.printf("Total discount: ₱%.2f\n", discountAmount);
        System.out.println("---------------------------------------");
        System.out.printf("Total amount due: ₱%.2f\n", total);
        System.out.printf("Cash received: ₱%.2f\n", payment);
        System.out.printf("Change: ₱%.2f\n", (payment - total));
        System.out.println("=======================================");
    }
}
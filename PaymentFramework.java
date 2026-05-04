abstract class PaymentFramework {
    protected double baseTicketPrice;
    protected int numberOfVisitors;
    static final double DISCOUNT_RATE = 0.20;
    private static final double VAT_RATE = 0.12;

    public PaymentFramework(double baseTicketPrice, int numberOfVisitors) {
        this.baseTicketPrice = baseTicketPrice;
        this.numberOfVisitors = numberOfVisitors;
    }

    public final void processPayment(double paymentAmount) {
        try {
            double subtotal = calculateSubtotal(); 
            double discountedTotal = applyDiscount(subtotal);

            if (!validatePayment(paymentAmount, discountedTotal)) {
                throw new Exception("Insufficient funds. Total due: " + String.format("%.2f", discountedTotal));
            }

            finalizeTransaction(paymentAmount, discountedTotal);
            processInvoice(discountedTotal, paymentAmount);

        } catch (Exception e) {
            System.out.println("Transaction Failed: " + e.getMessage());
        } finally {
            System.out.println("Session closed.");
        }
    }

    protected double calculateSubtotal() {
        return baseTicketPrice * numberOfVisitors;
    }

    protected double applyDiscount(double amount) {
        return amount - (amount * DISCOUNT_RATE);
    }

    protected boolean validatePayment(double payment, double total) {
        return payment >= total;
    }

    protected void finalizeTransaction(double payment, double total) {
        System.out.printf("Payment Accepted. Change: %.2f\n", (payment - total));
    }

    public void processInvoice(double total, double payment) {
        double grossAmount = baseTicketPrice * numberOfVisitors;
        double discountValue = grossAmount * DISCOUNT_RATE;
        
        double net = total / (1 + VAT_RATE);
        double vatAmount = total - net;

        System.out.println("\n--- MUSEUM INVOICE ---");
        System.out.printf("Visitors:       %d\n", numberOfVisitors);
        System.out.printf("Gross Total:    %.2f\n", grossAmount);
        System.out.printf("Discount (20%%): -%.2f\n", discountValue);
        System.out.println("---------------------------------------");
        System.out.printf("Net Amount:     %.2f\n", net);
        System.out.printf("VAT (12%%):      %.2f\n", vatAmount);
        System.out.printf("Total Amount:   %.2f\n", total);
        System.out.println("---------------------------------------");
        System.out.printf("Cash:           %.2f\n", payment);
        System.out.printf("Change:         %.2f\n", (payment - total));
        System.out.println("=======================================");
    }
}
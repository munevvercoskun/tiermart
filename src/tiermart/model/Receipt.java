package tiermart.model;

/**
 * The result of a purchase: what was bought, the discount that applied, and the
 * tier before/after (so the UI can announce a promotion).
 */
public record Receipt(
        String customerId,
        String productName,
        int quantity,
        long listCents,        // price * qty, before discount
        double discountRate,   // the rate that applied
        long paidCents,        // what the customer actually paid
        String tierBefore,
        String tierAfter) {

    public boolean promoted() { return !tierBefore.equals(tierAfter); }

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d x %s%n", quantity, productName));
        sb.append(String.format("  list:     $%.2f%n", listCents / 100.0));
        sb.append(String.format("  discount: %.0f%% (%s tier)%n", discountRate * 100, tierBefore));
        sb.append(String.format("  paid:     $%.2f%n", paidCents / 100.0));
        if (promoted()) {
            sb.append(String.format("  >> promoted %s -> %s!%n", tierBefore, tierAfter));
        }
        return sb.toString();
    }
}

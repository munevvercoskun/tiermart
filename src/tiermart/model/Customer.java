package tiermart.model;

import tiermart.model.membership.BasicState;
import tiermart.model.membership.MembershipState;

/**
 * A store customer -- the <em>context</em> in the State design pattern.
 *
 * <p>Overview: a Customer has a wallet (available funds, in cents), a lifetime
 * spend (money actually paid, after discounts), and a membership state object.
 * The customer never decides its own tier; it delegates to its {@link MembershipState}
 * and re-reviews after any change to lifetime spend. The abstraction function is:
 * a Customer represents a shopper whose discount is {@code state.discountRate()}
 * and whose tier is {@code state.name()}.
 *
 * <p>Rep invariant: {@code walletCents >= 0 && totalSpentCents >= 0 && state != null}.
 */
public class Customer {

    private final String id;
    private final String name;
    private long walletCents;
    private long totalSpentCents;
    private MembershipState state;

    /**
     * @requires id != null && name != null && walletCents >= 0
     * @effects creates a new customer in the Basic tier with the given wallet
     */
    public Customer(String id, String name, long walletCents) {
        if (id == null || name == null || walletCents < 0) {
            throw new IllegalArgumentException("invalid customer fields");
        }
        this.id = id;
        this.name = name;
        this.walletCents = walletCents;
        this.totalSpentCents = 0;
        this.state = new BasicState();
    }

    public String id()             { return id; }
    public String name()           { return name; }
    public long walletCents()      { return walletCents; }
    public long totalSpentCents()  { return totalSpentCents; }
    public MembershipState state() { return state; }
    public String tier()           { return state.name(); }
    public double discountRate()   { return state.discountRate(); }

    /**
     * @requires newState != null
     * @modifies this
     * @effects replaces this customer's membership state. Called by the state
     *          objects themselves during {@code review}; the State pattern lets
     *          a state swap in its successor.
     */
    public void setState(MembershipState newState) {
        if (newState == null) throw new IllegalArgumentException("null state");
        this.state = newState;
    }

    /**
     * @requires amountCents >= 0
     * @modifies this
     * @effects adds funds to the wallet
     */
    public void topUp(long amountCents) {
        if (amountCents < 0) throw new IllegalArgumentException("negative top-up");
        walletCents += amountCents;
    }

    /**
     * @requires paidCents >= 0 && paidCents <= walletCents
     * @modifies this
     * @effects deducts paidCents from the wallet, adds it to lifetime spend,
     *          then re-evaluates the membership tier (which may promote the
     *          customer, even by several tiers for a large purchase).
     */
    public void recordPurchase(long paidCents) {
        if (paidCents < 0 || paidCents > walletCents) {
            throw new IllegalArgumentException("insufficient funds / bad amount");
        }
        walletCents -= paidCents;
        totalSpentCents += paidCents;
        reviewUntilStable();
    }

    /**
     * @requires refundCents >= 0 && refundCents <= totalSpentCents
     * @modifies this
     * @effects returns refundCents to the wallet, removes it from lifetime spend,
     *          and re-evaluates the tier (which may demote the customer).
     */
    public void recordRefund(long refundCents) {
        if (refundCents < 0 || refundCents > totalSpentCents) {
            throw new IllegalArgumentException("bad refund amount");
        }
        walletCents += refundCents;
        totalSpentCents -= refundCents;
        reviewUntilStable();
    }

    /**
     * @modifies this
     * @effects keeps asking the current state to review until the tier settles,
     *          so a single big change can move several tiers at once while each
     *          state still only knows about its neighbours.
     */
    private void reviewUntilStable() {
        for (int guard = 0; guard < 8; guard++) {   // at most 4 tiers, so this is safe
            MembershipState before = state;
            state.review(this);
            if (state == before) return;            // no transition -> settled
        }
    }

    public String walletString() { return String.format("$%.2f", walletCents / 100.0); }
    public String spentString()  { return String.format("$%.2f", totalSpentCents / 100.0); }
}

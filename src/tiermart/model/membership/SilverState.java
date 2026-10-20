package tiermart.model.membership;

import tiermart.model.Customer;

/** 5% discount. Up to Gold at $500, back down to Basic below $100 (e.g. after a refund). */
public class SilverState implements MembershipState {

    @Override public String name() { return "Silver"; }

    @Override public double discountRate() { return Tiers.SILVER_RATE; }

    @Override
    public void review(Customer c) {
        long spent = c.totalSpentCents();
        if (spent >= Tiers.GOLD_AT_CENTS) {
            c.setState(new GoldState());
        } else if (spent < Tiers.SILVER_AT_CENTS) {
            c.setState(new BasicState());
        }
    }
}

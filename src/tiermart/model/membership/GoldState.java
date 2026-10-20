package tiermart.model.membership;

import tiermart.model.Customer;

/** 10% discount. Up to Platinum at $1000, back down to Silver below $500. */
public class GoldState implements MembershipState {

    @Override public String name() { return "Gold"; }

    @Override public double discountRate() { return Tiers.GOLD_RATE; }

    @Override
    public void review(Customer c) {
        long spent = c.totalSpentCents();
        if (spent >= Tiers.PLATINUM_AT_CENTS) {
            c.setState(new PlatinumState());
        } else if (spent < Tiers.GOLD_AT_CENTS) {
            c.setState(new SilverState());
        }
    }
}

package tiermart.model.membership;

import tiermart.model.Customer;

/** Top tier: 15% discount. Drops back to Gold if lifetime spend falls below $1000. */
public class PlatinumState implements MembershipState {

    @Override public String name() { return "Platinum"; }

    @Override public double discountRate() { return Tiers.PLATINUM_RATE; }

    @Override
    public void review(Customer c) {
        if (c.totalSpentCents() < Tiers.PLATINUM_AT_CENTS) {
            c.setState(new GoldState());
        }
    }
}

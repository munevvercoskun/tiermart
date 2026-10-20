package tiermart.model.membership;

import tiermart.model.Customer;

/** Entry tier: no discount. Promotes to Silver once lifetime spend reaches $100. */
public class BasicState implements MembershipState {

    @Override public String name() { return "Basic"; }

    @Override public double discountRate() { return Tiers.BASIC_RATE; }

    @Override
    public void review(Customer c) {
        if (c.totalSpentCents() >= Tiers.SILVER_AT_CENTS) {
            c.setState(new SilverState());   // one step up; Customer re-reviews for bigger jumps
        }
    }
}

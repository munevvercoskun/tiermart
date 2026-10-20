package tiermart.model.membership;

import tiermart.model.Customer;

/**
 * The State in the State design pattern (COE 528).
 *
 * <p>Overview: a customer's membership tier is not a flag that the rest of the
 * code switches on with if/else chains. Instead each tier is its own object
 * that knows two things: the discount it grants, and -- given a customer's
 * lifetime spend -- which tier the customer should now be in. The {@link Customer}
 * is the <em>context</em>; it delegates both questions to its current state and
 * lets the state swap itself out. Adding a new tier later means adding one class,
 * not editing a switch statement in five places (open/closed principle).
 */
public interface MembershipState {

    /** @effects returns the human-readable tier name, e.g. "Gold". */
    String name();

    /** @effects returns this tier's discount rate in [0,1], e.g. 0.10 for 10%. */
    double discountRate();

    /**
     * @requires customer != null && customer's state is this
     * @modifies customer
     * @effects promotes or demotes the customer to the correct tier for its
     *          current lifetime spend, by setting the customer's state. May
     *          leave the state unchanged if no transition is warranted.
     */
    void review(Customer customer);
}

package tiermart.service;

import tiermart.model.Customer;
import tiermart.model.Product;
import tiermart.model.Receipt;
import tiermart.model.Store;

/**
 * The business rules of the store -- the one place purchases, refunds and
 * catalogue changes are decided.
 *
 * <p>Overview: this is the application layer. The UI (CLI or GUI) only ever
 * calls these methods; it never reaches into the model directly. That keeps the
 * same tested logic behind both front-ends (single responsibility, and the UIs
 * depend on this service, not the other way around -- dependency inversion).
 *
 * <p>Each mutating method is written design-by-contract: a @requires precondition
 * the caller must meet, @modifies for what it changes, and @effects for the result.
 */
public class StoreService {

    private final Store store;

    public StoreService(Store store) {
        this.store = store;
    }

    public Store store() { return store; }

    /**
     * @requires id not already used && name != null && priceCents >= 0 && stock >= 0
     * @modifies this.store
     * @effects adds a new product to the catalogue
     * @throws IllegalArgumentException if the id is already taken
     */
    public Product addProduct(String id, String name, long priceCents, int stock) {
        if (store.hasProduct(id)) throw new IllegalArgumentException("product id in use: " + id);
        Product p = new Product(id, name, priceCents, stock);
        store.putProduct(p);
        return p;
    }

    /**
     * @requires id not already used && name != null && openingWalletCents >= 0
     * @modifies this.store
     * @effects registers a new customer in the Basic tier
     * @throws IllegalArgumentException if the id is already taken
     */
    public Customer addCustomer(String id, String name, long openingWalletCents) {
        if (store.hasCustomer(id)) throw new IllegalArgumentException("customer id in use: " + id);
        Customer c = new Customer(id, name, openingWalletCents);
        store.putCustomer(c);
        return c;
    }

    /**
     * Buy {@code qty} of a product for a customer.
     *
     * @requires the customer and product exist && qty >= 1 && product has
     *           enough stock && the customer can afford the discounted total
     * @modifies the customer (wallet, lifetime spend, possibly tier) and the
     *           product (stock)
     * @effects applies the customer's current-tier discount, charges the
     *          discounted total, reduces stock, re-evaluates the tier, and
     *          returns a Receipt describing the transaction
     * @throws IllegalArgumentException / IllegalStateException if a precondition fails
     */
    public Receipt purchase(String customerId, String productId, int qty) {
        Customer c = require(store.customer(customerId), "no such customer: " + customerId);
        Product p = require(store.product(productId), "no such product: " + productId);
        if (qty < 1) throw new IllegalArgumentException("quantity must be >= 1");
        if (qty > p.stock()) throw new IllegalStateException("not enough stock of " + p.name());

        long list = p.priceCents() * qty;
        double rate = c.discountRate();                 // the discount BEFORE this purchase
        long paid = Math.round(list * (1.0 - rate));
        if (paid > c.walletCents()) {
            throw new IllegalStateException(c.name() + " cannot afford this (needs "
                    + money(paid) + ", has " + c.walletString() + ")");
        }

        String tierBefore = c.tier();
        p.reduceStock(qty);
        c.recordPurchase(paid);                         // this may promote the customer
        String tierAfter = c.tier();

        return new Receipt(customerId, p.name(), qty, list, rate, paid, tierBefore, tierAfter);
    }

    /**
     * Refund a given amount of past spend to a customer.
     *
     * @requires the customer exists && 0 <= amountCents <= customer's lifetime spend
     * @modifies the customer (wallet, lifetime spend, possibly tier)
     * @effects returns the money to the wallet and re-evaluates the tier,
     *          which may demote the customer
     */
    public void refund(String customerId, long amountCents) {
        Customer c = require(store.customer(customerId), "no such customer: " + customerId);
        c.recordRefund(amountCents);
    }

    private static <T> T require(T value, String message) {
        if (value == null) throw new IllegalArgumentException(message);
        return value;
    }

    private static String money(long cents) { return String.format("$%.2f", cents / 100.0); }
}

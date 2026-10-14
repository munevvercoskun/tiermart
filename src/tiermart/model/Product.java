package tiermart.model;

/**
 * A product in the store catalogue.
 *
 * <p>Overview: a Product is an immutable description (id, name, unit price)
 * together with a mutable stock quantity. Prices are held in whole cents to
 * avoid floating-point rounding on money.
 */
public class Product {

    private final String id;
    private final String name;
    private final long priceCents;
    private int stock;

    /**
     * @requires id != null && name != null && priceCents >= 0 && stock >= 0
     * @effects creates a Product with the given fields
     */
    public Product(String id, String name, long priceCents, int stock) {
        if (id == null || name == null || priceCents < 0 || stock < 0) {
            throw new IllegalArgumentException("invalid product fields");
        }
        this.id = id;
        this.name = name;
        this.priceCents = priceCents;
        this.stock = stock;
    }

    public String id()        { return id; }
    public String name()      { return name; }
    public long priceCents()  { return priceCents; }
    public int stock()        { return stock; }

    /**
     * @requires n >= 1 && n <= stock
     * @modifies this
     * @effects reduces the stock by n
     */
    public void reduceStock(int n) {
        if (n < 1 || n > stock) throw new IllegalArgumentException("bad stock reduction");
        stock -= n;
    }

    /**
     * @requires n >= 1
     * @modifies this
     * @effects increases the stock by n (used when a purchase is refunded)
     */
    public void restock(int n) {
        if (n < 1) throw new IllegalArgumentException("bad restock");
        stock += n;
    }

    public String priceString() {
        return String.format("$%.2f", priceCents / 100.0);
    }
}

package tiermart.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The store's data: its product catalogue and its customers.
 *
 * <p>Overview: a plain in-memory holder keyed by id. Kept separate from the
 * business rules (those live in {@code StoreService}) so the model doesn't know
 * how it's used -- single-responsibility, and it makes swapping in a database
 * later a change in one layer only.
 */
public class Store {

    private final Map<String, Product> products = new LinkedHashMap<>();
    private final Map<String, Customer> customers = new LinkedHashMap<>();

    public void putProduct(Product p)   { products.put(p.id(), p); }
    public Product product(String id)   { return products.get(id); }
    public boolean hasProduct(String id){ return products.containsKey(id); }
    public Collection<Product> products() { return products.values(); }

    public void putCustomer(Customer c)   { customers.put(c.id(), c); }
    public Customer customer(String id)   { return customers.get(id); }
    public boolean hasCustomer(String id) { return customers.containsKey(id); }
    public Collection<Customer> customers() { return customers.values(); }
}

package tiermart.app;

import java.util.Scanner;

import tiermart.model.Customer;
import tiermart.model.Product;
import tiermart.model.Receipt;
import tiermart.service.SampleData;
import tiermart.service.StoreService;

/**
 * A text menu over the StoreService. Same business logic as the GUI -- this is
 * just a different front-end, which is the whole point of keeping the rules in
 * the service layer.
 */
public class Cli {

    private final StoreService s;
    private final Scanner in = new Scanner(System.in);

    public Cli(StoreService s) { this.s = s; }

    public static void main(String[] args) {
        new Cli(SampleData.seeded()).run();
    }

    public void run() {
        System.out.println("=== TierMart (COE 528 -- State pattern store) ===");
        loop:
        while (true) {
            System.out.print("\n[p]roducts  [c]ustomers  [b]uy  [r]efund  "
                    + "[P]add product  [C]add customer  [q]uit > ");
            String cmd = in.nextLine().trim();
            try {
                switch (cmd) {
                    case "p" -> listProducts();
                    case "c" -> listCustomers();
                    case "b" -> buy();
                    case "r" -> refund();
                    case "P" -> addProduct();
                    case "C" -> addCustomer();
                    case "q" -> { break loop; }
                    case ""  -> {}
                    default  -> System.out.println("unknown command");
                }
            } catch (Exception e) {
                System.out.println("  ! " + e.getMessage());
            }
        }
        System.out.println("bye.");
    }

    private void listProducts() {
        System.out.printf("%-4s %-22s %10s %6s%n", "id", "name", "price", "stock");
        for (Product p : s.store().products()) {
            System.out.printf("%-4s %-22s %10s %6d%n", p.id(), p.name(), p.priceString(), p.stock());
        }
    }

    private void listCustomers() {
        System.out.printf("%-4s %-12s %-10s %-9s %-10s %-10s%n",
                "id", "name", "tier", "discount", "wallet", "spent");
        for (Customer c : s.store().customers()) {
            System.out.printf("%-4s %-12s %-10s %-9s %-10s %-10s%n",
                    c.id(), c.name(), c.tier(), (int) (c.discountRate() * 100) + "%",
                    c.walletString(), c.spentString());
        }
    }

    private void buy() {
        String cid = ask("customer id");
        String pid = ask("product id");
        int qty = Integer.parseInt(ask("quantity"));
        Receipt r = s.purchase(cid, pid, qty);
        System.out.print(r.format());
    }

    private void refund() {
        String cid = ask("customer id");
        double dollars = Double.parseDouble(ask("refund amount ($)"));
        s.refund(cid, Math.round(dollars * 100));
        Customer c = s.store().customer(cid);
        System.out.printf("  refunded. %s is now %s (%s spent)%n", c.name(), c.tier(), c.spentString());
    }

    private void addProduct() {
        String id = ask("new product id");
        String name = ask("name");
        double price = Double.parseDouble(ask("price ($)"));
        int stock = Integer.parseInt(ask("stock"));
        s.addProduct(id, name, Math.round(price * 100), stock);
        System.out.println("  added " + name);
    }

    private void addCustomer() {
        String id = ask("new customer id");
        String name = ask("name");
        double wallet = Double.parseDouble(ask("opening wallet ($)"));
        s.addCustomer(id, name, Math.round(wallet * 100));
        System.out.println("  added " + name + " (Basic tier)");
    }

    private String ask(String prompt) {
        System.out.print("  " + prompt + ": ");
        return in.nextLine().trim();
    }
}

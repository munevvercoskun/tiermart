package tiermart.app;

import tiermart.model.Customer;
import tiermart.model.Receipt;
import tiermart.service.SampleData;
import tiermart.service.StoreService;

/**
 * A scripted walk through the State pattern -- the project's "prove it works"
 * demo. It drives a customer up the tiers with purchases and back down with a
 * refund, checking the discount and tier at each step. No test framework: it
 * prints a table and exits non-zero if any check fails, so it works anywhere
 * Java runs (`make demo`).
 */
public class SelfCheck {

    private static int failures = 0;

    public static void main(String[] args) {
        StoreService s = SampleData.seeded();
        Customer c = s.store().customer("C1");

        System.out.println("TierMart self-check -- State pattern transitions\n");
        System.out.printf("%-26s %-10s %-9s %-10s%n", "action", "tier", "discount", "spent");
        line();
        show("start", c);
        check("starts Basic", c.tier().equals("Basic"));

        // Buy a $89 keyboard: spend crosses $0 -> still Basic (threshold is $100).
        buy(s, "C1", "P2", 1);
        show("buy keyboard ($89)", c);
        check("still Basic under $100", c.tier().equals("Basic"));

        // Buy a $279 monitor: lifetime spend now $368 -> Silver.
        buy(s, "C1", "P3", 1);
        show("buy monitor ($279)", c);
        check("Silver after $100", c.tier().equals("Silver"));
        check("5% discount applied", c.discountRate() == 0.05);

        // Buy a 2nd monitor at 5% off: spend crosses $500 -> Gold.
        buy(s, "C1", "P3", 1);
        show("buy monitor (5% off)", c);
        check("Gold after $500", c.tier().equals("Gold"));

        // Buy two more monitors at 10% off: crosses $1000 -> Platinum.
        buy(s, "C1", "P3", 2);
        show("buy 2 monitors (10% off)", c);
        check("Platinum after $1000", c.tier().equals("Platinum"));
        check("15% discount applied", c.discountRate() == 0.15);

        // Refund $900 of spend: should fall back toward Silver.
        s.refund("C1", 900_00);
        show("refund $900", c);
        check("demoted below Platinum after refund", !c.tier().equals("Platinum"));

        line();
        if (failures == 0) {
            System.out.println("\nAll checks passed -- the State pattern promotes and demotes correctly.");
        } else {
            System.out.println("\n" + failures + " check(s) FAILED.");
            System.exit(1);
        }
    }

    private static void buy(StoreService s, String cust, String prod, int qty) {
        Receipt r = s.purchase(cust, prod, qty);
        if (r.promoted()) System.out.println("   (promotion: " + r.tierBefore() + " -> " + r.tierAfter() + ")");
    }

    private static void show(String action, Customer c) {
        System.out.printf("%-26s %-10s %-9s %-10s%n",
                action, c.tier(), (int) (c.discountRate() * 100) + "%", c.spentString());
    }

    private static void check(String label, boolean ok) {
        if (!ok) { failures++; System.out.println("   CHECK FAILED: " + label); }
    }

    private static void line() { System.out.println("-".repeat(60)); }
}

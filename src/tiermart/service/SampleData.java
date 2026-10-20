package tiermart.service;

/**
 * Seeds a store with a few products and customers so the CLI and GUI have
 * something to show on first launch.
 */
public final class SampleData {
    private SampleData() {}

    public static StoreService seeded() {
        StoreService s = new StoreService(new tiermart.model.Store());
        s.addProduct("P1", "USB-C cable",      12_99, 100);
        s.addProduct("P2", "Mechanical keyboard", 89_00, 25);
        s.addProduct("P3", "27\" monitor",    279_00, 10);
        s.addProduct("P4", "Laptop stand",     34_50, 40);
        s.addProduct("P5", "Webcam",           59_99, 30);

        s.addCustomer("C1", "Mona",   2_000_00);   // $2000 wallet
        s.addCustomer("C2", "Devran", 1_500_00);
        return s;
    }
}

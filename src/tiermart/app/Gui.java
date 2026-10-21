package tiermart.app;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import tiermart.model.Customer;
import tiermart.model.Product;
import tiermart.model.Receipt;
import tiermart.service.SampleData;
import tiermart.service.StoreService;

/**
 * A Swing desktop front-end over the same StoreService.
 *
 * <p>It's a thin shell: pick a customer, pick a product, choose a quantity, hit
 * Buy. The customer's tier and discount update live so you can watch a shopper
 * climb from Basic to Platinum. All it does is call the service and redraw --
 * no business logic lives here.
 */
public class Gui extends JFrame {

    private final StoreService s;
    private final JComboBox<String> customerBox = new JComboBox<>();
    private final JComboBox<String> productBox = new JComboBox<>();
    private final JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JLabel tierLabel = new JLabel();
    private final DefaultTableModel custModel =
            new DefaultTableModel(new Object[]{"id", "name", "tier", "discount", "wallet", "spent"}, 0);
    private final JTextArea log = new JTextArea(8, 40);

    public Gui(StoreService s) {
        super("TierMart -- COE 528 (State pattern store)");
        this.s = s;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        add(buildControls(), BorderLayout.NORTH);
        add(new JScrollPane(buildCustomerTable()), BorderLayout.CENTER);
        add(buildLog(), BorderLayout.SOUTH);
        refresh();
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildControls() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        for (Customer c : s.store().customers()) customerBox.addItem(c.id());
        for (Product pr : s.store().products()) productBox.addItem(pr.id() + " - " + pr.name());
        JButton buy = new JButton("Buy");
        buy.addActionListener(e -> doBuy());
        JButton refund = new JButton("Refund $100");
        refund.addActionListener(e -> doRefund());
        p.add(new JLabel("Customer:")); p.add(customerBox);
        p.add(new JLabel("Product:"));  p.add(productBox);
        p.add(new JLabel("Qty:"));      p.add(qtySpinner);
        p.add(buy); p.add(refund);
        tierLabel.setFont(tierLabel.getFont().deriveFont(Font.BOLD));
        p.add(tierLabel);
        customerBox.addActionListener(e -> updateTierLabel());
        return p;
    }

    private JTable buildCustomerTable() {
        JTable t = new JTable(custModel);
        t.setEnabled(false);
        return t;
    }

    private JScrollPane buildLog() {
        log.setEditable(false);
        return new JScrollPane(log);
    }

    private void doBuy() {
        try {
            String cid = (String) customerBox.getSelectedItem();
            String pid = ((String) productBox.getSelectedItem()).split(" - ")[0];
            int qty = (Integer) qtySpinner.getValue();
            Receipt r = s.purchase(cid, pid, qty);
            log.append(r.format() + "\n");
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Can't buy", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doRefund() {
        try {
            String cid = (String) customerBox.getSelectedItem();
            s.refund(cid, 100_00);
            Customer c = s.store().customer(cid);
            log.append("Refunded $100.00 to " + c.name() + " -> now " + c.tier() + "\n\n");
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Can't refund", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refresh() {
        custModel.setRowCount(0);
        for (Customer c : s.store().customers()) {
            custModel.addRow(new Object[]{c.id(), c.name(), c.tier(),
                    (int) (c.discountRate() * 100) + "%", c.walletString(), c.spentString()});
        }
        updateTierLabel();
    }

    private void updateTierLabel() {
        String cid = (String) customerBox.getSelectedItem();
        if (cid == null) return;
        Customer c = s.store().customer(cid);
        tierLabel.setText("  " + c.name() + ": " + c.tier()
                + " (" + (int) (c.discountRate() * 100) + "% off)");
    }

    public static void main(String[] args) {
        StoreService s = SampleData.seeded();
        SwingUtilities.invokeLater(() -> new Gui(s).setVisible(true));
    }
}

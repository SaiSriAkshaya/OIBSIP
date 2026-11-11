import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/** Cancellation form: fetch a booking by PNR, confirm, then delete it. */
public class CancellationPanel extends JPanel {
    private final JTextField pnrField = new JTextField(14);
    private final JTextArea detailsArea = new JTextArea(9, 30);
    private final JButton cancelBtn = new JButton("Cancel Ticket");
    private Reservation current;

    public CancellationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("PNR Number:"));
        top.add(pnrField);
        JButton fetchBtn = new JButton("Fetch");
        top.add(fetchBtn);

        detailsArea.setEditable(false);
        detailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        detailsArea.setText("Enter a PNR number and click Fetch.");

        cancelBtn.setEnabled(false);
        JPanel bottom = new JPanel();
        bottom.add(cancelBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        fetchBtn.addActionListener(e -> fetch());
        cancelBtn.addActionListener(e -> cancel());
    }

    private void fetch() {
        String text = pnrField.getText().trim();
        current = null;
        cancelBtn.setEnabled(false);

        if (text.isEmpty()) {
            error("Please enter a PNR number.");
            return;
        }
        long pnr;
        try {
            pnr = Long.parseLong(text);
        } catch (NumberFormatException e) {
            error("PNR must be numeric.");
            return;
        }
        try {
            Reservation r = Database.findReservation(pnr);
            if (r == null) {
                detailsArea.setText("No booking found for PNR " + pnr + ".");
                return;
            }
            current = r;
            detailsArea.setText(r.toDetails());
            cancelBtn.setEnabled(true);
        } catch (SQLException ex) {
            error("Database error: " + ex.getMessage());
        }
    }

    private void cancel() {
        if (current == null) return;
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this ticket?\n\n" + current.toDetails(),
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            if (Database.deleteReservation(current.getPnr())) {
                JOptionPane.showMessageDialog(this, "Ticket cancelled successfully.",
                        "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                detailsArea.setText("Ticket with PNR " + current.getPnr() + " has been cancelled.");
                pnrField.setText("");
            } else {
                error("Booking could not be found (it may already be cancelled).");
            }
            current = null;
            cancelBtn.setEnabled(false);
        } catch (SQLException ex) {
            error("Database error: " + ex.getMessage());
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

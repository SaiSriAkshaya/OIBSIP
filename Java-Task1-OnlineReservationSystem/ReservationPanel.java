import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Reservation form: validates input, saves to the database, shows the generated PNR. */
public class ReservationPanel extends JPanel {
    private final JTextField nameField = new JTextField(20);
    private final JTextField trainNoField = new JTextField(20);
    private final JTextField trainNameField = new JTextField(20);
    private final JComboBox<String> classBox = new JComboBox<>(new String[]{
            "Sleeper (SL)", "Third AC (3A)", "Second AC (2A)", "First AC (1A)", "Chair Car (CC)"});
    private final JTextField dateField = new JTextField(20);
    private final JTextField fromField = new JTextField(20);
    private final JTextField toField = new JTextField(20);

    public ReservationPanel() {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(15, 20, 15, 20));
        trainNameField.setEditable(false);

        int row = 0;
        addRow("Passenger Name:", nameField, row++);
        addRow("Train Number:", trainNoField, row++);
        addRow("Train Name (auto):", trainNameField, row++);
        addRow("Class Type:", classBox, row++);
        addRow("Date (yyyy-MM-dd):", dateField, row++);
        addRow("From (Source):", fromField, row++);
        addRow("To (Destination):", toField, row++);

        JButton bookBtn = new JButton("Book Ticket");
        JButton clearBtn = new JButton("Clear");
        JPanel buttons = new JPanel();
        buttons.add(bookBtn);
        buttons.add(clearBtn);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        gc.insets = new Insets(12, 6, 6, 6);
        add(buttons, gc);

        // Auto-populate the train name while the train number is typed
        trainNoField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateTrainName(); }
            public void removeUpdate(DocumentEvent e) { updateTrainName(); }
            public void changedUpdate(DocumentEvent e) { updateTrainName(); }
        });

        bookBtn.addActionListener(e -> book());
        clearBtn.addActionListener(e -> clearForm());
    }

    private void addRow(String label, java.awt.Component field, int row) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 6, 5, 6);
        gc.anchor = GridBagConstraints.WEST;
        gc.gridy = row;
        gc.gridx = 0;
        add(new JLabel(label), gc);
        gc.gridx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        add(field, gc);
    }

    private void updateTrainName() {
        String text = trainNoField.getText().trim();
        if (text.isEmpty()) {
            trainNameField.setText("");
            return;
        }
        try {
            String name = Database.findTrainName(Integer.parseInt(text));
            trainNameField.setText(name != null ? name : "Train not found");
        } catch (NumberFormatException e) {
            trainNameField.setText("Invalid train number");
        } catch (SQLException e) {
            trainNameField.setText("Database error");
        }
    }

    private void book() {
        String passenger = nameField.getText().trim();
        String trainNoText = trainNoField.getText().trim();
        String dateText = dateField.getText().trim();
        String source = fromField.getText().trim();
        String destination = toField.getText().trim();

        if (passenger.isEmpty() || trainNoText.isEmpty() || dateText.isEmpty()
                || source.isEmpty() || destination.isEmpty()) {
            error("Please fill in all the fields.");
            return;
        }
        int trainNo;
        try {
            trainNo = Integer.parseInt(trainNoText);
        } catch (NumberFormatException e) {
            error("Train number must be numeric.");
            return;
        }
        LocalDate date;
        try {
            date = LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            error("Invalid date. Use the format yyyy-MM-dd (e.g. 2026-12-25).");
            return;
        }
        if (date.isBefore(LocalDate.now())) {
            error("Date of journey cannot be in the past.");
            return;
        }
        if (source.equalsIgnoreCase(destination)) {
            error("Source and destination cannot be the same.");
            return;
        }

        try {
            String trainName = Database.findTrainName(trainNo);
            if (trainName == null) {
                error("Train number " + trainNo + " was not found.");
                return;
            }
            Reservation r = new Reservation(0, passenger, trainNo, trainName,
                    (String) classBox.getSelectedItem(), date.toString(), source, destination);
            Database.saveReservation(r);
            JOptionPane.showMessageDialog(this,
                    "Booking confirmed!\n\n" + r.toDetails() + "\n\nKeep your PNR for cancellation.",
                    "Reservation Successful", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        } catch (SQLException ex) {
            error("Database error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        nameField.setText("");
        trainNoField.setText("");
        trainNameField.setText("");
        classBox.setSelectedIndex(0);
        dateField.setText("");
        fromField.setText("");
        toField.setText("");
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

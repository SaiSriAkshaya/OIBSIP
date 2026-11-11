import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/** Main window with two tabs: Reservation and Cancellation. */
public class MainFrame extends JFrame {
    public MainFrame(String username) {
        super("Online Reservation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Book Ticket", new ReservationPanel());
        tabs.addTab("Cancel Ticket", new CancellationPanel());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(new JLabel("Logged in as: " + username));
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });
        top.add(logout);

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        setSize(520, 470);
        setLocationRelativeTo(null);
    }
}

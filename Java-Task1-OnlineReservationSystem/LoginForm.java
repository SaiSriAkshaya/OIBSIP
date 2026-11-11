import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/** Login form: username + password. Access denied for invalid credentials. */
public class LoginForm extends JFrame {
    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);

    public LoginForm() {
        super("Online Reservation System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Train Reservation Login");
        title.setFont(title.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        panel.add(title, gc);

        gc.gridwidth = 1; gc.anchor = GridBagConstraints.WEST;
        gc.gridy = 1; gc.gridx = 0; panel.add(new JLabel("Username:"), gc);
        gc.gridx = 1; panel.add(usernameField, gc);
        gc.gridy = 2; gc.gridx = 0; panel.add(new JLabel("Password:"), gc);
        gc.gridx = 1; panel.add(passwordField, gc);

        JButton loginBtn = new JButton("Login");
        gc.gridy = 3; gc.gridx = 0; gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        panel.add(loginBtn, gc);

        JLabel hint = new JLabel("Demo login: admin / admin123");
        hint.setFont(hint.getFont().deriveFont(11f));
        gc.gridy = 4;
        panel.add(hint, gc);

        loginBtn.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(loginBtn);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Missing Details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (Database.validateLogin(username, password)) {
                dispose();
                new MainFrame(username).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Access denied: invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Entry point: prepares the database and opens the login form. */
public class Main {
    public static void main(String[] args) {
        try {
            Database.init();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not open the database:\n" + e.getMessage()
                    + "\n\nMake sure the sqlite-jdbc jar is in the lib folder and on the classpath.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}

import java.util.HashMap;
import java.util.Map;

/** Holds all accounts and handles authentication and lookups. */
public class Bank {
    private final Map<String, Account> accounts = new HashMap<>();

    public Bank() {
        // Demo accounts for testing
        addAccount(new Account("user1", "1234", 5000));
        addAccount(new Account("user2", "5678", 3000));
        addAccount(new Account("user3", "4321", 10000));
    }

    public void addAccount(Account account) {
        accounts.put(account.getUserId(), account);
    }

    public Account findAccount(String userId) {
        return accounts.get(userId);
    }

    /** Returns the account if ID and PIN match, otherwise null. */
    public Account authenticate(String userId, String pin) {
        Account acc = accounts.get(userId);
        return (acc != null && acc.checkPin(pin)) ? acc : null;
    }
}

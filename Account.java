import java.util.ArrayList;
import java.util.List;

/** A bank account with a PIN, balance and transaction history (encapsulated). */
public class Account {
    private final String userId;
    private final String pin;
    private double balance;
    private final List<Transaction> history = new ArrayList<>();

    public Account(String userId, String pin, double openingBalance) {
        this.userId = userId;
        this.pin = pin;
        this.balance = openingBalance;
    }

    public String getUserId() { return userId; }
    public double getBalance() { return balance; }
    public List<Transaction> getHistory() { return history; }

    public boolean checkPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public boolean hasSufficientFunds(double amount) {
        return amount <= balance;
    }

    public void deposit(double amount) {
        balance += amount;
        history.add(new Transaction("DEPOSIT", amount, "Cash deposit"));
    }

    public void withdraw(double amount) {
        balance -= amount;
        history.add(new Transaction("WITHDRAW", amount, "Cash withdrawal"));
    }

    /** Moves money to another account and logs it on both sides. */
    public void transfer(Account to, double amount) {
        this.balance -= amount;
        to.balance += amount;
        this.history.add(new Transaction("TRANSFER", amount, "Sent to " + to.getUserId()));
        to.history.add(new Transaction("RECEIVED", amount, "Received from " + this.userId));
    }
}

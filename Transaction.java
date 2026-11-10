import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a single ATM transaction (one entry in the history log). */
public class Transaction {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final String type;
    private final double amount;
    private final String details;
    private final LocalDateTime timestamp;

    public Transaction(String type, double amount, String details) {
        this.type = type;
        this.amount = amount;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDetails() { return details; }

    @Override
    public String toString() {
        return String.format("[%s] %-10s Rs.%,10.2f  %s",
                timestamp.format(FMT), type, amount, details);
    }
}

import java.util.List;
import java.util.Scanner;

/** The ATM menu for a logged-in user. */
public class ATM {
    private final Bank bank;
    private final Account account;
    private final Scanner scanner;

    public ATM(Bank bank, Account account, Scanner scanner) {
        this.bank = bank;
        this.account = account;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": showHistory(); break;
                case "2": withdraw(); break;
                case "3": deposit(); break;
                case "4": transfer(); break;
                case "5":
                    System.out.println("\nThank you for banking with us. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-5.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===== ATM MENU =====");
        System.out.println("1. Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Quit");
        System.out.print("Choose an option: ");
    }

    private void showHistory() {
        List<Transaction> history = account.getHistory();
        System.out.println("\n--- Transaction History ---");
        if (history.isEmpty()) {
            System.out.println("No transactions in this session.");
        } else {
            for (Transaction t : history) {
                System.out.println(t);
            }
        }
        System.out.printf("Current balance: Rs.%,.2f%n", account.getBalance());
    }

    private void withdraw() {
        double amount = readAmount("Enter amount to withdraw: ");
        if (amount < 0) return;
        if (!account.hasSufficientFunds(amount)) {
            System.out.println("Insufficient Funds.");
            return;
        }
        account.withdraw(amount);
        System.out.printf("Please collect your cash. New balance: Rs.%,.2f%n", account.getBalance());
    }

    private void deposit() {
        double amount = readAmount("Enter amount to deposit: ");
        if (amount < 0) return;
        account.deposit(amount);
        System.out.printf("Deposit successful. New balance: Rs.%,.2f%n", account.getBalance());
    }

    private void transfer() {
        System.out.print("Enter recipient account ID: ");
        String recipientId = scanner.nextLine().trim();
        Account recipient = bank.findAccount(recipientId);
        if (recipient == null) {
            System.out.println("Recipient account not found.");
            return;
        }
        if (recipient == account) {
            System.out.println("You cannot transfer to your own account.");
            return;
        }
        double amount = readAmount("Enter amount to transfer: ");
        if (amount < 0) return;
        if (!account.hasSufficientFunds(amount)) {
            System.out.println("Insufficient Funds.");
            return;
        }
        account.transfer(recipient, amount);
        System.out.printf("Transferred Rs.%,.2f to %s. New balance: Rs.%,.2f%n",
                amount, recipientId, account.getBalance());
    }

    /** Reads a positive amount. Returns -1 if the input is invalid. */
    private double readAmount(String prompt) {
        System.out.print(prompt);
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount <= 0) {
                System.out.println("Amount must be greater than zero.");
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.");
            return -1;
        }
    }
}

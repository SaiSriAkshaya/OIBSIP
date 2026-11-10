import java.util.Scanner;

/** Entry point: login with User ID and PIN (3 attempts), then open the ATM. */
public class Main {
    private static final int MAX_ATTEMPTS = 3;

    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== WELCOME TO THE ATM ===");
        System.out.println("(Demo logins: user1/1234, user2/5678, user3/4321)");

        Account account = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            System.out.print("\nEnter User ID: ");
            String userId = scanner.nextLine().trim();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            account = bank.authenticate(userId, pin);
            if (account != null) break;

            int left = MAX_ATTEMPTS - attempt;
            if (left > 0) {
                System.out.println("Invalid User ID or PIN. Attempts left: " + left);
            }
        }

        if (account == null) {
            System.out.println("\nToo many failed attempts. Access denied.");
        } else {
            System.out.println("\nLogin successful. Welcome, " + account.getUserId() + "!");
            new ATM(bank, account, scanner).run();
        }
        scanner.close();
    }
}

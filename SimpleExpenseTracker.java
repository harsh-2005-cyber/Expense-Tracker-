import java.util.*;
import java.text.SimpleDateFormat;

public class SimpleExpenseTracker {
    private static List<Expense> expenses = new ArrayList<>();
    private static List<Income> incomes = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        System.out.println("💰 Simple Expense Tracker");
        System.out.println("=========================");
        
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    addExpense();
                    break;
                case 2:
                    addIncome();
                    break;
                case 3:
                    showSummary();
                    break;
                case 4:
                    showAllTransactions();
                    break;
                case 5:
                    running = false;
                    System.out.println("Thank you for using Simple Expense Tracker!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n=== Menu ===");
        System.out.println("1. Add Expense");
        System.out.println("2. Add Income");
        System.out.println("3. Show Summary");
        System.out.println("4. Show All Transactions");
        System.out.println("5. Exit");
        System.out.println("============");
    }

    private static void addExpense() {
        System.out.println("\n--- Add Expense ---");
        String description = getStringInput("Description: ");
        double amount = getDoubleInput("Amount: ");
        String category = getStringInput("Category: ");
        
        Expense expense = new Expense(description, amount, new Date(), category);
        expenses.add(expense);
        System.out.println("Expense added successfully!");
    }

    private static void addIncome() {
        System.out.println("\n--- Add Income ---");
        String source = getStringInput("Source: ");
        double amount = getDoubleInput("Amount: ");
        
        Income income = new Income(source, amount, new Date());
        incomes.add(income);
        System.out.println("Income added successfully!");
    }

    private static void showSummary() {
        System.out.println("\n--- Financial Summary ---");
        
        double totalExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double totalIncome = incomes.stream().mapToDouble(Income::getAmount).sum();
        double savings = totalIncome - totalExpenses;
        
        System.out.printf("Total Income: $%.2f\n", totalIncome);
        System.out.printf("Total Expenses: $%.2f\n", totalExpenses);
        System.out.printf("Savings: $%.2f\n", savings);
        
        if (savings >= 0) {
            System.out.println("🎉 You're saving money!");
        } else {
            System.out.println("⚠️  You're spending more than you earn!");
        }
    }

    private static void showAllTransactions() {
        System.out.println("\n--- All Transactions ---");
        
        System.out.println("\nExpenses:");
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {
            for (Expense expense : expenses) {
                System.out.printf("%s - %s: $%.2f (%s)\n", 
                    dateFormat.format(expense.getDate()),
                    expense.getDescription(),
                    expense.getAmount(),
                    expense.getCategory());
            }
        }

        System.out.println("\nIncome:");
        if (incomes.isEmpty()) {
            System.out.println("No income recorded.");
        } else {
            for (Income income : incomes) {
                System.out.printf("%s - %s: $%.2f\n", 
                    dateFormat.format(income.getDate()),
                    income.getSource(),
                    income.getAmount());
            }
        }
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }
}

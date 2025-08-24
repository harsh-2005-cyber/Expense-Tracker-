import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class ExpenseTracker {
    private static TransactionManager transactionManager;
    private static Scanner scanner;

    public static void main(String[] args) {
        transactionManager = new TransactionManager();
        scanner = new Scanner(System.in);
        
        System.out.println("Welcome to the Expense Tracker!");
        
        // Test case: Adding an expense and income
        try {
            Date expenseDate = new SimpleDateFormat("yyyy-MM-dd").parse("2023-10-01");
            Expense testExpense = new Expense("Groceries", 150.00, expenseDate, "Food");
            transactionManager.addExpense(testExpense);
            
            Date incomeDate = new SimpleDateFormat("yyyy-MM-dd").parse("2023-10-01");
            Income testIncome = new Income("Salary", 2000.00, incomeDate);
            transactionManager.addIncome(testIncome);
        } catch (ParseException e) {
            System.out.println("Error parsing date: " + e.getMessage());
        }

        System.out.printf("Total Savings: $%.2f\n", transactionManager.calculateSavings());

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
                    viewMonthlySummary();
                    break;
                case 4:
                    viewSavings();
                    break;
                case 5:
                    viewAllTransactions();
                    break;
                case 6:
                    running = false;
                    System.out.println("Thank you for using Expense Tracker!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n=== Expense Tracker Menu ===");
        System.out.println("1. Add Expense");
        System.out.println("2. Add Income");
        System.out.println("3. View Monthly Summary");
        System.out.println("4. View Total Savings");
        System.out.println("5. View All Transactions");
        System.out.println("6. Exit");
        System.out.println("============================");
    }

    private static void addExpense() {
        System.out.println("\n--- Add New Expense ---");
        String description = getStringInput("Description: ");
        double amount = getDoubleInput("Amount: ");
        Date date = getDateInput("Date (yyyy-MM-dd): ");
        String category = getStringInput("Category: ");

        Expense expense = new Expense(description, amount, date, category);
        transactionManager.addExpense(expense);
        System.out.println("Expense added successfully!");
    }

    private static void addIncome() {
        System.out.println("\n--- Add New Income ---");
        String source = getStringInput("Source: ");
        double amount = getDoubleInput("Amount: ");
        Date date = getDateInput("Date (yyyy-MM-dd): ");

        Income income = new Income(source, amount, date);
        transactionManager.addIncome(income);
        System.out.println("Income added successfully!");
    }

    private static void viewMonthlySummary() {
        System.out.println("\n--- Monthly Summary ---");
        int year = getIntInput("Enter year (e.g., 2024): ");
        int month = getIntInput("Enter month (1-12): ") - 1; // Calendar months are 0-based
        
        String summary = transactionManager.getMonthlySummary(month, year);
        System.out.println(summary);
    }

    private static void viewSavings() {
        System.out.println("\n--- Total Savings ---");
        double totalIncome = transactionManager.calculateTotalIncome();
        double totalExpenses = transactionManager.calculateTotalExpenses();
        double savings = transactionManager.calculateSavings();
        
        System.out.printf("Total Income: $%.2f\n", totalIncome);
        System.out.printf("Total Expenses: $%.2f\n", totalExpenses);
        System.out.printf("Total Savings: $%.2f\n", savings);
    }

    private static void viewAllTransactions() {
        System.out.println("\n--- All Transactions ---");
        
        System.out.println("\nExpenses:");
        List<Expense> expenses = transactionManager.getExpenses();
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Expense expense : expenses) {
                System.out.printf("%s - %s: $%.2f (%s)\n", 
                    dateFormat.format(expense.getDate()),
                    expense.getDescription(),
                    expense.getAmount(),
                    expense.getCategory());
            }
        }

        System.out.println("\nIncome:");
        List<Income> incomes = transactionManager.getIncomes();
        if (incomes.isEmpty()) {
            System.out.println("No income recorded.");
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }

    private static Date getDateInput(String prompt) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        
        while (true) {
            try {
                System.out.print(prompt);
                String dateString = scanner.nextLine().trim();
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                System.out.println("Please enter a valid date in yyyy-MM-dd format.");
            }
        }
    }
}

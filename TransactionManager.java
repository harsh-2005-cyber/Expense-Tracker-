import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.io.*;
import java.text.SimpleDateFormat;

public class TransactionManager {
    private List<Expense> expenses;
    private List<Income> incomes;
    private static final String DATA_FILE = "transactions.dat";

    public TransactionManager() {
        expenses = new ArrayList<>();
        incomes = new ArrayList<>();
        loadTransactions();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        saveTransactions();
    }

    public void addIncome(Income income) {
        incomes.add(income);
        saveTransactions();
    }

    public double calculateTotalExpenses() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }

    public double calculateTotalIncome() {
        double total = 0;
        for (Income income : incomes) {
            total += income.getAmount();
        }
        return total;
    }

    public double calculateSavings() {
        return calculateTotalIncome() - calculateTotalExpenses();
    }

    public double calculateMonthlyExpenses(int month, int year) {
        double total = 0;
        for (Expense expense : expenses) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(expense.getDate());
            if (cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year) {
                total += expense.getAmount();
            }
        }
        return total;
    }

    public double calculateMonthlyIncome(int month, int year) {
        double total = 0;
        for (Income income : incomes) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(income.getDate());
            if (cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year) {
                total += income.getAmount();
            }
        }
        return total;
    }

    public double calculateMonthlySavings(int month, int year) {
        return calculateMonthlyIncome(month, year) - calculateMonthlyExpenses(month, year);
    }

    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    public List<Income> getIncomes() {
        return new ArrayList<>(incomes);
    }

    @SuppressWarnings("unchecked")
    private void loadTransactions() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            expenses = (List<Expense>) ois.readObject();
            incomes = (List<Income>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }

    private void saveTransactions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(expenses);
            oos.writeObject(incomes);
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    public String getMonthlySummary(int month, int year) {
        double monthlyIncome = calculateMonthlyIncome(month, year);
        double monthlyExpenses = calculateMonthlyExpenses(month, year);
        double monthlySavings = calculateMonthlySavings(month, year);

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        
        return String.format("Monthly Summary for %s:\n" +
                           "Total Income: $%.2f\n" +
                           "Total Expenses: $%.2f\n" +
                           "Savings: $%.2f",
                           monthFormat.format(cal.getTime()),
                           monthlyIncome,
                           monthlyExpenses,
                           monthlySavings);
    }
}

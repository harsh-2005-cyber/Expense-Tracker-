import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class ExpenseTrackerServer {
    private static final int PORT = 8080;
    private static TransactionManager transactionManager;

    public static void main(String[] args) {
        transactionManager = new TransactionManager();
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Expense Tracker Server started on port " + PORT);
            System.out.println("Open http://localhost:" + PORT + " in your browser");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            String request = in.readLine();
            if (request == null) return;

            if (request.startsWith("GET / ")) {
                serveFile(out, "index.html", "text/html");
            } else if (request.startsWith("GET /api/transactions")) {
                handleGetTransactions(out);
            } else if (request.startsWith("POST /api/transactions")) {
                handlePostTransaction(in, out);
            } else if (request.startsWith("GET /api/summary")) {
                handleGetSummary(out);
            } else {
                sendNotFound(out);
            }
        } catch (IOException e) {
            System.err.println("Client handling error: " + e.getMessage());
        }
    }

    private static void serveFile(PrintWriter out, String filename, String contentType) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                sendNotFound(out);
                return;
            }

            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + contentType);
            out.println("Content-Length: " + content.length());
            out.println("Access-Control-Allow-Origin: *");
            out.println();
            out.println(content);
        } catch (IOException e) {
            sendError(out, "Error reading file: " + e.getMessage());
        }
    }

    private static void handleGetTransactions(PrintWriter out) {
        try {
            List<Expense> expenses = transactionManager.getExpenses();
            List<Income> incomes = transactionManager.getIncomes();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            StringBuilder json = new StringBuilder();
            json.append("{\"expenses\":[");
            for (int i = 0; i < expenses.size(); i++) {
                Expense expense = expenses.get(i);
                json.append(String.format(
                    "{\"description\":\"%s\",\"amount\":%.2f,\"date\":\"%s\",\"category\":\"%s\"}",
                    expense.getDescription().replace("\"", "\\\""),
                    expense.getAmount(),
                    dateFormat.format(expense.getDate()),
                    expense.getCategory().replace("\"", "\\\"")
                ));
                if (i < expenses.size() - 1) json.append(",");
            }
            
            json.append("],\"incomes\":[");
            for (int i = 0; i < incomes.size(); i++) {
                Income income = incomes.get(i);
                json.append(String.format(
                    "{\"source\":\"%s\",\"amount\":%.2f,\"date\":\"%s\"}",
                    income.getSource().replace("\"", "\\\""),
                    income.getAmount(),
                    dateFormat.format(income.getDate())
                ));
                if (i < incomes.size() - 1) json.append(",");
            }
            json.append("]}");
            
            sendJsonResponse(out, json.toString());
        } catch (Exception e) {
            sendError(out, "Error getting transactions: " + e.getMessage());
        }
    }

    private static void handlePostTransaction(BufferedReader in, PrintWriter out) {
        try {
            // Read the request body
            StringBuilder body = new StringBuilder();
            while (in.ready()) {
                body.append((char) in.read());
            }

            // Simple JSON parsing (in a real application, use a JSON library)
            String bodyStr = body.toString();
            if (bodyStr.contains("\"type\":\"expense\"")) {
                // Parse expense
                String description = extractValue(bodyStr, "description");
                double amount = Double.parseDouble(extractValue(bodyStr, "amount"));
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(extractValue(bodyStr, "date"));
                String category = extractValue(bodyStr, "category");
                
                Expense expense = new Expense(description, amount, date, category);
                transactionManager.addExpense(expense);
            } else if (bodyStr.contains("\"type\":\"income\"")) {
                // Parse income
                String source = extractValue(bodyStr, "source");
                double amount = Double.parseDouble(extractValue(bodyStr, "amount"));
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(extractValue(bodyStr, "date"));
                
                Income income = new Income(source, amount, date);
                transactionManager.addIncome(income);
            }
            
            sendJsonResponse(out, "{\"status\":\"success\"}");
        } catch (Exception e) {
            sendError(out, "Error processing transaction: " + e.getMessage());
        }
    }

    private static void handleGetSummary(PrintWriter out) {
        try {
            double totalIncome = transactionManager.calculateTotalIncome();
            double totalExpenses = transactionManager.calculateTotalExpenses();
            double savings = transactionManager.calculateSavings();
            
            String json = String.format(
                "{\"totalIncome\":%.2f,\"totalExpenses\":%.2f,\"savings\":%.2f}",
                totalIncome, totalExpenses, savings
            );
            
            sendJsonResponse(out, json);
        } catch (Exception e) {
            sendError(out, "Error getting summary: " + e.getMessage());
        }
    }

    private static String extractValue(String json, String key) {
        int start = json.indexOf("\"" + key + "\":") + key.length() + 3;
        int end = json.indexOf("\"", start);
        if (end == -1) end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return json.substring(start, end).replace("\"", "");
    }

    private static void sendJsonResponse(PrintWriter out, String json) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println("Access-Control-Allow-Origin: *");
        out.println("Content-Length: " + json.length());
        out.println();
        out.println(json);
    }

    private static void sendError(PrintWriter out, String message) {
        out.println("HTTP/1.1 500 Internal Server Error");
        out.println("Content-Type: text/plain");
        out.println();
        out.println(message);
    }

    private static void sendNotFound(PrintWriter out) {
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/plain");
        out.println();
        out.println("404 Not Found");
    }
}

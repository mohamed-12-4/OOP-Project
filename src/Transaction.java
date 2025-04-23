import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transaction {
    private UUID transactionId;
    private UUID userId;       // Foreign key to the users table
    private String type;       // "income" or "expense"
    private double amount;
    private Date date;         // java.util.Date; convert to java.sql.Date for DB operations
    private String category;   // Relevant for expenses; can also be used to categorize income if desired
    private String source;     // Relevant for income (e.g., salary, allowance); may be null for expenses
    private String description;

    public Transaction() {}
    // Constructor to create a new Transaction
    public Transaction(UUID userId, String type, double amount, Date date, String category, String source, String description) {
        this.transactionId = UUID.randomUUID();
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.source = source;
        this.description = description;
    }

    // Getters
    public UUID getTransactionId() { return transactionId; }
    public UUID getUserId() { return userId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
    public String getCategory() { return category; }
    public String getSource() { return source; }
    public String getDescription() { return description; }

    // Static method to insert this transaction into the 'transactions' table
    public static void addTransaction(Transaction tx) {
        String query = "INSERT INTO transactions (user_id, type, amount, date, category, source, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {


            ps.setObject(1, tx.getUserId());
            ps.setString(2, tx.getType());
            ps.setDouble(3, tx.getAmount());
            ps.setDate(4,  new Date(tx.getDate().getTime()));
            ps.setString(5, tx.getCategory());
            ps.setString(6, tx.getSource());
            ps.setString(7, tx.getDescription());
            ps.executeUpdate();

            if ("expense".equalsIgnoreCase(tx.getType())) {
                Budget.updateActualSpent(tx.getUserId(), tx.getCategory());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    // Static method to delete a transaction from the 'transactions' table by its transaction_id
    public static void deleteTransaction(UUID transactionId) {
        String query = "DELETE FROM transactions WHERE transaction_id = ?";
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setObject(1, transactionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<Transaction> getTransactions(UUID userId) {
        ArrayList<Transaction> transactions = new ArrayList<>();

        try {
            Connection conn = NeonDBConnection.getConnection();
            String query = "SELECT * FROM transactions WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setObject(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                String date = rs.getString("date");
                String amount = rs.getString("amount");
                String description = rs.getString("description");
                String category = rs.getString("category");
                String source = rs.getString("source");
                transactions.add(new Transaction(userId, type, Double.parseDouble(amount), Date.valueOf(date), category, source, description));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}

class Income extends Transaction {}
class Expense extends Transaction {}
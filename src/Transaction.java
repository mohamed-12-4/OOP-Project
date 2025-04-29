import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transaction {
    private int transactionId;
    private UUID userId;       // Foreign key to the users table
    private String type;       // "income" or "expense"
    private double amount;
    private Date date;         // java.util.Date; convert to java.sql.Date for DB operations
    private String category;   // Relevant for expenses; can also be used to categorize income if desired
    private String source;     // Relevant for income (e.g., salary, allowance); may be null for expenses
    private String description;

    public void setAmount(double amount) {
        this.amount = amount;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Transaction() {}
    public Transaction(int txId) {
        this.transactionId = txId;
        String query = "SELECT *FROM transactions WHERE id = ?";
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setObject(1, txId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("user_id"));
                this.userId = id;
                this.type = rs.getString("type");
                this.amount = rs.getDouble("amount");
                this.date = rs.getDate("date");
                this.category = rs.getString("category");
                this.source = rs.getString("source");
                this.description = rs.getString("description");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    // Constructor to create a new Transaction
    public Transaction(UUID userId, String type, double amount, Date date, String category, String source, String description) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.source = source;
        this.description = description;
    }
    public Transaction(int trxId, UUID userId, String type, double amount, Date date, String category, String source, String description) {
        this.transactionId = trxId;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.source = source;
        this.description = description;
    }

    public static double getTotalIncome(UUID id) {
        double total = 0;
        String query = "SELECT SUM(amount) FROM transactions WHERE type='income' AND user_id = ?";
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                total += rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;

    }

    public static double getTotalExpenses(UUID id) {
        double total = 0;
        String query = "SELECT SUM(amount) FROM transactions WHERE type='expense' AND user_id = ?" ;
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                total += rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // Getters
    public int getTransactionId() { return transactionId; }
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
    public static void deleteTransaction(Transaction tx) {
        String query = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setObject(1, tx.getTransactionId());
            ps.executeUpdate();
            if ("expense".equalsIgnoreCase(tx.getType())) {
                Budget.updateActualSpent(tx.getUserId(), tx.getCategory());
            }
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
                int transactionId = rs.getInt("id");
                String type = rs.getString("type");
                String date = rs.getString("date");
                String amount = rs.getString("amount");
                String description = rs.getString("description");
                String category = rs.getString("category");
                String source = rs.getString("source");
                transactions.add(new Transaction(transactionId, userId, type, Double.parseDouble(amount), Date.valueOf(date), category, source, description));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    public static ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();

        try {
            Connection conn = NeonDBConnection.getConnection();
            String query = "SELECT * FROM transactions";
            PreparedStatement ps = conn.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int transactionId =rs.getInt("id");
                UUID userId = UUID.fromString(rs.getString("user_id"));
                String type = rs.getString("type");
                String date = rs.getString("date");
                String amount = rs.getString("amount");
                String description = rs.getString("description");
                String category = rs.getString("category");
                String source = rs.getString("source");
                transactions.add(new Transaction(transactionId, userId, type, Double.parseDouble(amount), Date.valueOf(date), category, source, description));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public String toString() {
        if (type == "expense") {
            return String.format("Transcation ID: %d \t TransactionType: %s \n Transaction Amount: %.1f \n Transaction Source/Income: %s", transactionId, type, amount, category);
        }
        return String.format("Transcation ID: %d \t TransactionType: %s \n Transaction Amount: %.1f \n Transaction Source/Income: %s", transactionId, type, amount, source);
    }

    public void updateInDB() {
        String query = "UPDATE transactions SET type = ?, amount = ?, date = ?, category = ?, description = ? WHERE id = ?";
        try (Connection conn = NeonDBConnection.getConnection()){;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, type);
            ps.setDouble(2, amount);
            ps.setDate(3, date);
            ps.setString(4, category);
            ps.setObject(5, description);
            ps.setInt(6, transactionId);

            ps.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

class Income extends Transaction {}
class Expense extends Transaction {}
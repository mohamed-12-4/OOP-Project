import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Budget {
    private UUID budgetId;
    private UUID userId;
    private String category;
    private double setAmount;
    private double actualSpent;
    private double remainingAmount;
    private String status;

    public Budget(UUID userId, String category, double setAmount) {
        this.budgetId = UUID.randomUUID();
        this.userId = userId;
        this.category = category;
        this.setAmount = setAmount;
        this.actualSpent = 0.0;
        this.remainingAmount = setAmount;
        this.status = "Under Budget";
    }

    public UUID getBudgetId() { return budgetId; }
    public UUID getUserId() { return userId; }
    public String getCategory() { return category; }
    public double getSetAmount() { return setAmount; }
    public double getActualSpent() { return actualSpent; }
    public double getRemainingAmount() { return remainingAmount; }
    public String getStatus() { return status; }


    private void updateRemainingAndStatus() {
        this.remainingAmount = setAmount - actualSpent;
        this.status = (remainingAmount >= 0) ? "Under Budget" : "Over Budget";
    }

    // Add new budget
    public static void addBudget(Budget b) {
        String query = "INSERT INTO budget (budget_id, user_id, category, set_amount, actual_spent, remaining_amount, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, b.getBudgetId());
            ps.setObject(2, b.getUserId());
            ps.setString(3, b.getCategory());
            ps.setDouble(4, b.getSetAmount());
            ps.setDouble(5, b.actualSpent);
            ps.setDouble(6, b.remainingAmount);
            ps.setString(7, b.status);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update actual spent based on user's expenses in this category

    public static ArrayList<Budget> getBudgetsForUser(UUID userId) {
        ArrayList<Budget> budgets = new ArrayList<>();
        String query = "SELECT * FROM budget WHERE user_id = ?";

        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setObject(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID budgetId = UUID.fromString(rs.getString("budget_id"));
                String category = rs.getString("category");
                double setAmount = rs.getDouble("set_amount");
                double actualSpent = rs.getDouble("actual_spent");
                double remainingAmount = rs.getDouble("remaining_amount");
                String status = rs.getString("status");

                Budget b = new Budget(userId, category, setAmount);
                b.budgetId = budgetId;
                b.actualSpent = actualSpent;
                b.remainingAmount = remainingAmount;
                b.status = status;

                budgets.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return budgets;
    }

    public static void updateActualSpent(UUID userId, String category) {
        double totalSpent = 0.0;

        // Sum expenses from the transactions table
        String query = "SELECT SUM(amount) FROM transactions WHERE user_id = ? AND type = 'expense' AND category = ?";
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, userId);
            ps.setString(2, category);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalSpent = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update the budget with actual spent
        String updateQuery = "UPDATE budget SET actual_spent = ?, remaining_amount = set_amount - ?, " +
                "status = CASE WHEN set_amount - ? >= 0 THEN 'Under Budget' ELSE 'Over Budget' END " +
                "WHERE user_id = ? AND category = ?";
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {
            ps.setDouble(1, totalSpent);
            ps.setDouble(2, totalSpent);
            ps.setDouble(3, totalSpent);
            ps.setObject(4, userId);
            ps.setString(5, category);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete a budget
    public static void deleteBudget(UUID budgetId) {
        String query = "DELETE FROM budget WHERE budget_id = ?";
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setObject(1, budgetId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
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
    private String approvalStatus;
    public Budget(UUID budgetId) {
        try {
            Connection conn = NeonDBConnection.getConnection();
            String query = "SELECT * FROM budget WHERE budget_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setObject(1, budgetId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                budgetId = UUID.fromString(rs.getString("budget_id"));
                userId = UUID.fromString(rs.getString("user_id"));
                category = rs.getString("category");
                setAmount = rs.getDouble("set_amount");
                actualSpent = rs.getDouble("actual_spent");
                remainingAmount = rs.getDouble("remaining_amount");
                status = rs.getString("status");
                approvalStatus = rs.getString("pending");

            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Budget(UUID userId, String category, double setAmount) {
        this.budgetId = UUID.randomUUID();
        this.userId = userId;
        this.category = category;
        this.setAmount = setAmount;
        this.actualSpent = 0.0;
        this.remainingAmount = setAmount;
        this.status = "Under Budget";
    }

    public static List<Budget> getAllBudgets() {
        List<Budget> budgets = new ArrayList<>();
        try {
            Connection conn = NeonDBConnection.getConnection();
            String query = "SELECT * FROM budget";
            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID budgetId = UUID.fromString(rs.getString("budget_id"));

                Budget budget = new Budget(budgetId);
                budgets.add(budget);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;

    }

    public static int countByApproval(String pending) {
        int count = 0;
        try {
            Connection conn = NeonDBConnection.getConnection();
            String query = "SELECT COUNT(*) FROM budget WHERE pending = '" + pending + "'";
            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }



    public static void setApprovalStatusForUser(UUID userId, String newStatus) {
        String query = "UPDATE budget SET pending = '" + newStatus + "' WHERE user_Id = '" + userId + "'";
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setApprovalStatusForBudget(UUID userId, String category, String status) {
        try (Connection conn = NeonDBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE budget SET pending = ? WHERE user_id = ? AND category = ?");
            stmt.setString(1, status);
            stmt.setObject(2, userId);
            stmt.setString(3, category);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public UUID getBudgetId() { return budgetId; }
    public UUID getUserId() { return userId; }
    public String getCategory() { return category; }
    public double getSetAmount() { return setAmount; }
    public double getActualSpent() { return actualSpent; }
    public double getRemainingAmount() { return remainingAmount; }
    public String getStatus() { return status; }
    public String getApprovalStatus() { return approvalStatus; }


    private void updateRemainingAndStatus() {
        this.remainingAmount = setAmount - actualSpent;
        this.status = (remainingAmount >= 0) ? "Under Budget" : "Over Budget";
    }

    // Add new budget
    public static void addBudget(Budget b) {
        String query = "INSERT INTO budget (budget_id, user_id, category, set_amount, actual_spent, remaining_amount, status, pending) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending')";
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
    public static double planedForUser(UUID userId) {
        double planned = 0;
        String query = "SELECT SUM(set_amount) FROM budget WHERE user_id = ?";

        try {

            Connection conn = NeonDBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setObject(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                planned += rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return planned;

    }

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
                String approvalStatus = rs.getString("pending");

                Budget b = new Budget(budgetId);


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

    public void updateInDb(String category, double setAmount) {
        String query = "UPDATE budget SET category = ?, set_amount = ? WHERE budget_id = ?";
        try {
            Connection conn = NeonDBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, category);
            ps.setDouble(2, setAmount);
            ps.setObject(3, budgetId);
            ps.executeUpdate();
        }
        catch (SQLException e) {
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
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class AdminDashboard extends JFrame {
    private CardLayout cardLayout;
    private JPanel centerPanel;

    private DefaultTableModel userTableModel;
    private JTable userTable;

    private DefaultTableModel budgetTableModel;
    private JTable budgetTable;

    public AdminDashboard() {
        super("Admin Dashboard - Budget Management");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.add(createHeader(), BorderLayout.NORTH);
        main.add(createSidebar(), BorderLayout.WEST);
        main.add(createCenterPanel(), BorderLayout.CENTER);

        add(main);
        setVisible(true);
    }

    private JPanel createHeader() {
        JPanel p = new JPanel();
        p.setBackground(new Color(40, 58, 82));
        p.setPreferredSize(new Dimension(getWidth(), 60));
        JLabel lbl = new JLabel("Admin Dashboard");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.BOLD, 24));
        p.add(lbl);
        return p;
    }

    private JPanel createSidebar() {
        JPanel p = new JPanel();
        p.setBackground(new Color(50, 70, 100));
        p.setPreferredSize(new Dimension(200, getHeight()));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        String[] items = {"Dashboard", "Users", "Budgets", "Transactions", "Reports", "Settings"};
        for (String name : items) {
            JButton btn = new JButton(name);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(50, 70, 100));
            btn.setFont(new Font("Arial", Font.PLAIN, 16));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setMaximumSize(new Dimension(200, 40));
            btn.addActionListener(e -> {
                // Refresh data when switching
                if ("Users".equals(name)) {
                    refreshUserTable();
                } else if ("Budgets".equals(name)) {
                    refreshBudgetTable();
                }
                cardLayout.show(centerPanel, name);
            });
            p.add(btn);
            p.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return p;
    }


    private JPanel createCenterPanel() {
        centerPanel = new JPanel();
        cardLayout = new CardLayout();
        centerPanel.setLayout(cardLayout);

        centerPanel.add(createDashboardPanel(), "Dashboard");
        centerPanel.add(createUsersPanel(), "Users");
        centerPanel.add(createBudgetsPanel(), "Budgets");
        centerPanel.add(createTransactionsPanel(), "Transactions");
        centerPanel.add(createPlaceholder("Reports"), "Reports");
        centerPanel.add(createPlaceholder("Settings"), "Settings");

        return centerPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.add(createSummaryPanel(), BorderLayout.NORTH);
        return p;
    }
    private DefaultTableModel transactionsTableModel;
    private JTable transactionsTable;

    private JPanel createTransactionsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        p.setBackground(Color.WHITE);

        String[] cols = {"Transaction ID", "User ID", "Type", "Amount", "Date", "Category", "Source", "Description"};
        transactionsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionsTable = new JTable(transactionsTableModel);
        transactionsTable.setRowHeight(30);
        transactionsTable.setFont(new Font("Arial", Font.PLAIN, 14));

        refreshTransactionsTable();

        JButton deleteTransactionBtn = makeButton("Delete Transaction", new Color(220, 53, 69));
        deleteTransactionBtn.addActionListener(e -> deleteTransaction());

        p.add(new JScrollPane(transactionsTable), BorderLayout.CENTER);
        p.add(deleteTransactionBtn, BorderLayout.SOUTH);

        return p;
    }
    private void refreshTransactionsTable() {
        transactionsTableModel.setRowCount(0);
        for (Transaction tx : Transaction.getAllTransactions()) {
            transactionsTableModel.addRow(new Object[]{
                    String.valueOf(tx.getTransactionId()),
                    tx.getUserId().toString(),
                    tx.getType(),
                    tx.getAmount(),
                    tx.getDate().toString(),
                    tx.getCategory(),
                    tx.getSource(),
                    tx.getDescription()
            });
        }
    }


    private void deleteTransaction() {
        int row = transactionsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a transaction to delete.");
            return;
        }
        int txId = Integer.parseInt( (String) transactionsTableModel.getValueAt(row, 0));
        Transaction tx = new Transaction(txId);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this transaction?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Transaction.deleteTransaction(tx);
            refreshTransactionsTable();
            refreshUserTable();
            refreshBudgetTable();  // Deleting expense could affect budgets!
        }
    }




    private JPanel createSummaryPanel() {
        JPanel p = new JPanel(new GridLayout(1, 3, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.setBackground(Color.WHITE);

        JLabel totalUsers = new JLabel("Total Users: " + User.countUsers(), SwingConstants.CENTER);
        JLabel pendingBudgets = new JLabel("Pending Budgets: " + Budget.countByApproval("Pending"), SwingConstants.CENTER);
        JLabel approvedBudgets = new JLabel("Approved Budgets: " + Budget.countByApproval("Approved"), SwingConstants.CENTER);

        for (JLabel lbl : Arrays.asList(totalUsers, pendingBudgets, approvedBudgets)) {
            lbl.setFont(new Font("Arial", Font.BOLD, 18));
            lbl.setOpaque(true);
            lbl.setBackground(new Color(240, 240, 240));
            lbl.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            p.add(lbl);
        }

        return p;
    }

    private JPanel createUsersPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.setBackground(Color.WHITE);

        String[] cols = {"User ID", "Name", "Email", "Income", "Expenses", "Budget Status", "Approval Status"};
        userTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3 || column == 4) return Double.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(userTableModel);
        userTable.setRowHeight(30);
        userTable.setFont(new Font("Arial", Font.PLAIN, 14));

        refreshUserTable();
        p.add(new JScrollPane(userTable), BorderLayout.CENTER);
        p.add(createUserActionPanel(), BorderLayout.SOUTH);
        return p;
    }

    private void refreshUserTable() {
        userTableModel.setRowCount(0);
        for (User u : User.getAllUsers()) {
            UUID id = u.getUuid();
            double income = Transaction.getTotalIncome(id);
            double expense = Transaction.getTotalExpenses(id);
            String status = "Under Budget";
            String approvalStatus = "Approved";
            for (Budget b : Budget.getBudgetsForUser(id)) {
                if ("Over Budget".equals(b.getStatus())) { status = "Over Budget"; }
                if ("Rejected".equals(b.getApprovalStatus())) { approvalStatus = "Pending"; }
            }
            userTableModel.addRow(new Object[]{
                    id.toString(),
                    u.getName(),
                    u.getEmail(),
                    income,
                    expense,
                    status,
                    approvalStatus
            });
        }
    }

    private JPanel createUserActionPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        p.setBackground(Color.WHITE);


        JButton edit    = makeButton("Edit",    new Color(255, 193, 7));
        JButton del     = makeButton("Delete",  new Color(108, 117, 125));
        JButton search  = makeButton("Search",  new Color(0, 123, 255));
        JButton addTransaction = makeButton("+ Transaction", new Color(0, 204, 102));
        JButton addBudget = makeButton("+ Budget", new Color(102, 153, 255));


        edit   .addActionListener(e -> editUser());
        del    .addActionListener(e -> deleteUser());
        search .addActionListener(e -> searchUser());
        addTransaction.addActionListener(e -> openAddTransactionDialog());
        addBudget.addActionListener(e -> openAddBudgetDialog());




        for (JButton b : Arrays.asList(edit, del, search, addTransaction, addBudget)) {
            p.add(b);
        }
        return p;
    }

    private void openAddTransactionDialog() {
        int row = userTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user."); return; }
        UUID userId = UUID.fromString((String) userTable.getValueAt(row, 0));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(Color.WHITE);

        String[] types = {"income", "expense"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        JTextField amountField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JTextField categoryField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField sourceField = new JTextField();

        panel.add(new JLabel("Type:"));
        panel.add(typeBox);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Date:"));
        panel.add(dateField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Source:"));
        panel.add(sourceField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Transaction", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Transaction tx = new Transaction(
                        userId,
                        typeBox.getSelectedItem().toString(),
                        Double.parseDouble(amountField.getText()),
                        Date.valueOf(dateField.getText()),
                        categoryField.getText(),
                        sourceField.getText(),
                        descriptionField.getText()
                );
                Transaction.addTransaction(tx);
                JOptionPane.showMessageDialog(this, "Transaction added successfully.");
                refreshUserTable();
                refreshBudgetTable();  // Expense might affect budget!
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage());
            }
        }
    }

    private void openAddBudgetDialog() {
        int row = userTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user."); return; }
        UUID userId = UUID.fromString((String) userTable.getValueAt(row, 0));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(Color.WHITE);

        JTextField categoryField = new JTextField();
        JTextField setAmountField = new JTextField();

        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Set Amount:"));
        panel.add(setAmountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Budget", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Budget budget = new Budget(userId, categoryField.getText(), Double.parseDouble(setAmountField.getText()));
                Budget.addBudget(budget);
                JOptionPane.showMessageDialog(this, "Budget added successfully.");
                refreshBudgetTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage());
            }
        }
    }
    private void searchBudgetByStatus() {
        String[] options = {"Pending", "Approved", "Rejected"};
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Select budget approval status to filter:",
                "Search Budgets",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == null) return;

        for (int i = 0; i < budgetTableModel.getRowCount(); i++) {
            String status = budgetTableModel.getValueAt(i, 6).toString(); // Assuming Approval is column 6
            if (status.equalsIgnoreCase(choice)) {
                budgetTable.setRowSelectionInterval(i, i);
                budgetTable.scrollRectToVisible(budgetTable.getCellRect(i, 0, true));
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "No budget found with status: " + choice);
    }

    private void approveSelectedBudget() {
        int row = budgetTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a budget.");
            return;
        }

        UUID userId = UUID.fromString(budgetTableModel.getValueAt(row, 0).toString());
        String category = budgetTableModel.getValueAt(row, 1).toString();
        Budget.setApprovalStatusForBudget(userId, category, "Approved");
        refreshBudgetTable();
    }

    private void rejectSelectedBudget() {
        int row = budgetTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a budget.");
            return;
        }

        UUID userId = UUID.fromString(budgetTableModel.getValueAt(row, 0).toString());
        String category = budgetTableModel.getValueAt(row, 1).toString();
        Budget.setApprovalStatusForBudget(userId, category, "Rejected");
        refreshBudgetTable();
    }


    private JPanel createBudgetsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.setBackground(Color.WHITE);

        String[] cols = {
                "User ID", "Category", "Planned", "Spent", "Remaining", "Budget Status", "Approval Status"
        };
        budgetTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column >= 2 && column <= 4) return Double.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        budgetTable = new JTable(budgetTableModel);
        budgetTable.setRowHeight(30);
        budgetTable.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshBudgetTable();

        p.add(new JScrollPane(budgetTable), BorderLayout.CENTER);

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionPanel.setBackground(Color.WHITE);

        JButton approve = makeButton("Approve", new Color(40, 167, 69));
        JButton reject  = makeButton("Reject",  new Color(220, 53, 69));


        // Attach existing actions
        approve.addActionListener(e -> {
            if (budgetTable.isShowing()) {
                approveSelectedBudget();
            } else {
                changeApproval("Approved");
            }
        });

        reject.addActionListener(e -> {
            if (budgetTable.isShowing()) {
                rejectSelectedBudget();
            } else {
                changeApproval("Rejected");
            }
        });


        // Add to panel
        for (JButton btn : Arrays.asList(
                approve, reject
        )) {
            actionPanel.add(btn);
        }

        p.add(actionPanel, BorderLayout.SOUTH);

        return p;
    }

    private void refreshBudgetTable() {
        budgetTableModel.setRowCount(0);
        for (Budget b : Budget.getAllBudgets()) {
            budgetTableModel.addRow(new Object[]{
                    b.getUserId().toString(),
                    b.getCategory(),
                    b.getSetAmount(),
                    b.getActualSpent(),
                    b.getRemainingAmount(),
                    b.getStatus(),
                    b.getApprovalStatus()
            });
        }
    }

    private JPanel createPlaceholder(String name) {
        JPanel p = new JPanel();
        p.add(new JLabel(name + " page coming soon"));
        return p;
    }

    private void changeApproval(String status) {
        int row = userTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user."); return; }
        UUID id = UUID.fromString((String)userTable.getValueAt(row, 0));
        Budget.setApprovalStatusForUser(id, status);
        refreshUserTable();
        refreshBudgetTable();
    }

    private void editUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user."); return; }
        UUID id = UUID.fromString((String)userTable.getValueAt(row, 0));
        User u = new User(id);
        String newName  = JOptionPane.showInputDialog(this, "Name:",  u.getName());
        String newEmail = JOptionPane.showInputDialog(this, "Email:", u.getEmail());
        String newAddress = JOptionPane.showInputDialog(this, "Address:", u.getAddress());
        String newPhone = JOptionPane.showInputDialog(this, "Phone Number", u.getPhoneNum());
        if (newName != null && newEmail != null) {
            u.setName(newName);
            u.setEmail(newEmail);
            u.setAddress(newAddress);
            u.setPhoneNum(newPhone);
            u.updateInDB();
            refreshUserTable();
        }
    }

    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a user."); return; }
        UUID id = UUID.fromString((String)userTable.getValueAt(row, 0));
        if (JOptionPane.showConfirmDialog(
                this, "Delete this user?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            User.deleteById(id);
            refreshUserTable();
        }
    }

    private void searchUser() {
        String term = JOptionPane.showInputDialog(this, "Name or status:");
        if (term == null) return;
        for (int i = 0; i < userTableModel.getRowCount(); i++) {
            String name = (String)userTableModel.getValueAt(i, 1);
            String status = userTableModel.getValueAt(i, 5).toString();
            if (name.contains(term) || status.contains(term)) {
                userTable.setRowSelectionInterval(i, i);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "No match found.");
    }

    private JButton makeButton(String txt, Color bg) {
        JButton b = new JButton(txt);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(100, 40));
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}

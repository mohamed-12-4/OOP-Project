import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.function.BiFunction;

public class App extends JFrame {
    // Constants for styling
    private static final Color KU_BLUE = new Color(0, 84, 150); // KU Blue color
    private static final Color KU_GRAY = new Color(62, 62, 62); // Gray color
    private static final Color KU_LIGHT_GRAY = new Color(102, 102, 102); // Light gray color

    private static final Color KU_WHITE = new Color(255, 255, 255);
    private static final Color SUCCESS_COLOR = new Color(52, 199, 89);
    private static final Color KU_RED = new Color(255, 0, 0);
    private static final Color KU_LIGHT_BLUE = new Color(91, 128, 178); // Light blue color

    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 14);

    // File for storing user data
    private static final String USER_DATA_FILE = "user_data.txt";

    // Main components
    private JPanel sidebarPanel;
    private JPanel mainPanel;
    private JPanel summaryPanel;
    private JPanel budgetPanel;
    private JPanel transactionsPanel;

    // CardLayout components
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel dashboardPanel;
    private JPanel budgetViewPanel;
    private JPanel transactionsViewPanel;
    private JPanel reportsPanel;
    private JPanel goalsPanel;
    private JPanel settingsPanel;

    // Model data
    private double totalIncome = 0;
    private double plannedExpenses = 0;
    private double actualExpenses = 0;

    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

    // Budget table data
    private DefaultTableModel budgetTableModel;
    private JTable budgetTable;

    // Transactions data
    private DefaultListModel<String> transactionsListModel;
    private JList<String> transactionsList;

    public App(User user) {
        // Set up the main frame
        setTitle("Budget Planner");
        setSize(1200, 800);
        setResizable(true); // Disable resizing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(KU_WHITE);
        setVisible(true);




        // Create main components
        createSidebar(user);
        createMainPanel(user);

        // Add components to the frame
        add(sidebarPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }





    private void createSidebar(User user) {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // App logo/name
        JLabel logoLabel = new JLabel("Budget Planner");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 22));
        logoLabel.setForeground(KU_BLUE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Navigation menu items
        String[] menuItems = {"Dashboard", "Budget", "Transactions", "Reports", "Goals", "Settings"};
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String item : menuItems) {
            JPanel menuItemPanel = new JPanel(new BorderLayout());
            menuItemPanel.setBackground(Color.WHITE);
            menuItemPanel.setMaximumSize(new Dimension(200, 40));
            menuItemPanel.setName(item); // Set name for identification

            JLabel menuLabel = new JLabel(item);
            menuLabel.setFont(NORMAL_FONT);
            menuLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

            if (item.equals("Dashboard")) {
                menuItemPanel.setBackground(new Color(240, 240, 250));
                menuLabel.setForeground(KU_BLUE);
                menuLabel.setFont(new Font("Arial", Font.BOLD, 14));
            }

            menuItemPanel.add(menuLabel, BorderLayout.CENTER);
            menuItemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Add mouseListener for hover effects and card switching
            menuItemPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!item.equals("Dashboard")) {
                        menuItemPanel.setBackground(new Color(245, 245, 250));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!item.equals("Dashboard")) {
                        menuItemPanel.setBackground(Color.WHITE);
                    }
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    // Reset all menu items to default style
                    for (Component comp : menuPanel.getComponents()) {
                        if (comp instanceof JPanel) {
                            JPanel panel = (JPanel) comp;
                            panel.setBackground(Color.WHITE);
                            for (Component labelComp : panel.getComponents()) {
                                if (labelComp instanceof JLabel) {
                                    JLabel label = (JLabel) labelComp;
                                    label.setFont(NORMAL_FONT);
                                    label.setForeground(Color.BLACK);
                                }
                            }
                        }
                    }

                    // Style the clicked menu item
                    menuItemPanel.setBackground(new Color(240, 240, 250));
                    menuLabel.setForeground(KU_BLUE);
                    menuLabel.setFont(new Font("Arial", Font.BOLD, 14));

                    // Switch to the corresponding card
                    cardLayout.show(cardPanel, item);
                }
            });

            menuPanel.add(menuItemPanel);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // User profile section at bottom
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(Color.WHITE);
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel(user.getName());
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emailLabel = new JLabel(user.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        emailLabel.setForeground(Color.GRAY);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        userPanel.add(userLabel);
        userPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userPanel.add(emailLabel);

        // Add all elements to sidebar with spacing
        sidebarPanel.add(logoLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        sidebarPanel.add(menuPanel);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(new JSeparator());
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(userPanel);
    }

    private void createMainPanel(User user) {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(KU_WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Header with month selection
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(KU_WHITE);

        JLabel monthLabel = new JLabel("March 2025");
        monthLabel.setFont(HEADER_FONT);

        JPanel monthControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        monthControlPanel.setBackground(KU_WHITE);

        JButton prevButton = new JButton("<-");
        prevButton.setFocusPainted(false);
        prevButton.setBorderPainted(false);
        prevButton.setContentAreaFilled(false);
        prevButton.setFont(new Font("Arial", Font.BOLD, 16));
        prevButton.setForeground(KU_BLUE);

        JButton nextButton = new JButton("->");
        nextButton.setFocusPainted(false);
        nextButton.setBorderPainted(false);
        nextButton.setContentAreaFilled(false);
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.setForeground(KU_BLUE);

        monthControlPanel.add(monthLabel);
        monthControlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        monthControlPanel.add(prevButton);
        monthControlPanel.add(nextButton);

        JButton addTransactionButton = new JButton("+ Add Transaction");
        addTransactionButton.setBackground(KU_BLUE);
        addTransactionButton.setForeground(Color.WHITE);
        addTransactionButton.setFocusPainted(false);
        addTransactionButton.setBorderPainted(false);
        addTransactionButton.setFont(new Font("Arial", Font.BOLD, 14));
        addTransactionButton.addActionListener(e -> {createTransactionDialog(this, user.getUuid());});

        headerPanel.add(monthControlPanel, BorderLayout.WEST);
        headerPanel.add(addTransactionButton, BorderLayout.EAST);
        headerPanel.setBorder(new EmptyBorder(0,0,10,0));

        // Create card panel with CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(KU_WHITE);

        // Create dashboard panel (default view)
        dashboardPanel = createDashboardPanel();
        cardPanel.add(dashboardPanel, "Dashboard");

        // Create budget view panel
        budgetViewPanel = createBudgetViewPanel();
        cardPanel.add(budgetViewPanel, "Budget");

        // Create transactions view panel
        transactionsViewPanel = createTransactionsViewPanel();
        cardPanel.add(transactionsViewPanel, "Transactions");

        // Create other view panels (simplified for this example)
        reportsPanel = createPlaceholderPanel("Reports View");
        cardPanel.add(reportsPanel, "Reports");

        goalsPanel = createPlaceholderPanel("Goals View");
        cardPanel.add(goalsPanel, "Goals");

        settingsPanel = createSetting();
        cardPanel.add(settingsPanel, "Settings");

        // Add header and card panel to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(KU_WHITE);

        // Create summary cards panel
        createSummaryPanel();

        // Create budget categories panel
        createBudgetPanel();

        // Create recent transactions panel
        createTransactionsPanel();

        panel.add(summaryPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(budgetPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(transactionsPanel);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(KU_WHITE);
        scrollPane.getViewport().setBackground(KU_WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }


    private JPanel createBudgetViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(KU_WHITE);

        // Create a more detailed budget view
        JLabel title = new JLabel("Budget Management", JLabel.CENTER);
        title.setFont(HEADER_FONT);
        panel.add(title, BorderLayout.NORTH);

        // Add the budget table from the original dashboard
        createBudgetPanel();
        panel.add(budgetPanel, BorderLayout.CENTER);

        // Add some additional budget controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlsPanel.setBackground(KU_WHITE);

        JButton addButton = new JButton("Add New Budget Category");
        addButton.setBackground(KU_BLUE);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);

        controlsPanel.add(addButton);
        panel.add(controlsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTransactionsViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(KU_WHITE);

        // Create a more detailed transactions view
        JLabel title = new JLabel("Transaction History", JLabel.CENTER);
        title.setFont(HEADER_FONT);
        panel.add(title, BorderLayout.NORTH);

        // Add the transactions list from the original dashboard
        createTransactionsPanel();
        panel.add(transactionsPanel, BorderLayout.CENTER);

        // Add some additional transaction controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlsPanel.setBackground(KU_WHITE);

        JButton importButton = new JButton("Import Transactions");
        importButton.setBackground(KU_BLUE);
        importButton.setForeground(Color.WHITE);
        importButton.setFocusPainted(false);

        JButton exportButton = new JButton("Export Transactions");
        exportButton.setBackground(KU_BLUE);
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);

        controlsPanel.add(importButton);
        controlsPanel.add(exportButton);
        panel.add(controlsPanel, BorderLayout.SOUTH);

        return panel;
    }
    public static void createTransactionDialog(JFrame parent, UUID userId) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(KU_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Components
        JLabel header = new JLabel("Add New Transaction");
        header.setFont(HEADER_FONT);
        header.setForeground(KU_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(header, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(NORMAL_FONT);
        typeLabel.setForeground(KU_GRAY);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"income", "expense"});
        typeBox.setFont(NORMAL_FONT);
        typeBox.setBackground(KU_LIGHT_BLUE);
        typeBox.setForeground(KU_WHITE);
        typeBox.setFocusable(false);

        panel.add(typeLabel, gbc);
        gbc.gridx = 1;
        panel.add(typeBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(NORMAL_FONT);
        amountLabel.setForeground(KU_GRAY);
        JTextField amountField = new JTextField(12);
        amountField.setFont(NORMAL_FONT);
        panel.add(amountLabel, gbc);
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setFont(NORMAL_FONT);
        dateLabel.setForeground(KU_GRAY);
        JTextField dateField = new JTextField("2025-04-15", 12);
        dateField.setFont(NORMAL_FONT);
        panel.add(dateLabel, gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(NORMAL_FONT);
        categoryLabel.setForeground(KU_GRAY);
        JTextField categoryField = new JTextField(12);
        categoryField.setFont(NORMAL_FONT);
        panel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        panel.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel sourceLabel = new JLabel("Source (for income):");
        sourceLabel.setFont(NORMAL_FONT);
        sourceLabel.setForeground(KU_GRAY);
        JTextField sourceField = new JTextField(12);
        sourceField.setFont(NORMAL_FONT);
        panel.add(sourceLabel, gbc);
        gbc.gridx = 1;
        panel.add(sourceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(NORMAL_FONT);
        descLabel.setForeground(KU_GRAY);
        JTextField descField = new JTextField(12);
        descField.setFont(NORMAL_FONT);
        panel.add(descLabel, gbc);
        gbc.gridx = 1;
        panel.add(descField, gbc);

        // Show Dialog
        int result = JOptionPane.showConfirmDialog(parent, panel, "Create Transaction", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String type = (String) typeBox.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                java.sql.Date date = java.sql.Date.valueOf(dateField.getText());
                String category = categoryField.getText();
                String source = sourceField.getText();
                String description = descField.getText();

                Transaction tx = new Transaction(userId, type, amount, date, category, source, description);
                Transaction.addTransaction(tx);

                JOptionPane.showMessageDialog(parent, "Transaction added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Failed to add transaction:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(KU_WHITE);

        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(HEADER_FONT);
        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private void createSummaryPanel() {
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(KU_WHITE);
        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Income summary card
        JPanel incomePanel = createSummaryCard("Income", totalIncome, totalIncome, KU_BLUE);

        // Budgeted summary card
        JPanel budgetedPanel = createSummaryCard("Planned", plannedExpenses, totalIncome, KU_GRAY);

        // Spent summary card
        JPanel spentPanel = createSummaryCard("Spent", actualExpenses, plannedExpenses,
                actualExpenses > plannedExpenses ? KU_RED : SUCCESS_COLOR);

        summaryPanel.add(incomePanel);
        summaryPanel.add(budgetedPanel);
        summaryPanel.add(spentPanel);
    }

    private JPanel createSummaryCard(String title, double amount, double total, Color accentColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.DARK_GRAY);

        JLabel amountLabel = new JLabel(currencyFormat.format(amount));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 24));
        amountLabel.setForeground(accentColor);

        // Create progress bar or percentage display
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(Color.WHITE);

        double percentage = Math.min(100, (amount / total) * 100);
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) percentage);
        progressBar.setForeground(accentColor);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setPreferredSize(new Dimension(progressBar.getWidth(), 8));
        progressBar.setBorderPainted(false);

        JLabel percentLabel = new JLabel(String.format("%.1f%%", percentage));
        percentLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        percentLabel.setForeground(Color.GRAY);

        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(percentLabel, BorderLayout.EAST);

        // Add components to card
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(amountLabel, BorderLayout.CENTER);
        panel.add(progressPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void createBudgetPanel() {
        budgetPanel = new JPanel(new BorderLayout());
        budgetPanel.setBackground(Color.WHITE);
        budgetPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        budgetPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        budgetPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header section
        JPanel budgetHeaderPanel = new JPanel(new BorderLayout());
        budgetHeaderPanel.setBackground(Color.WHITE);

        JLabel budgetTitle = new JLabel("Budget Categories");
        budgetTitle.setFont(HEADER_FONT);

        JButton addCategoryButton = new JButton("+ Add Category");
        addCategoryButton.setBackground(Color.WHITE);
        addCategoryButton.setForeground(KU_BLUE);
        addCategoryButton.setBorder(new LineBorder(KU_BLUE, 1, true));
        addCategoryButton.setFocusPainted(false);

        budgetHeaderPanel.add(budgetTitle, BorderLayout.WEST);
        budgetHeaderPanel.add(addCategoryButton, BorderLayout.EAST);

        // Table column headers
        String[] columnNames = {"Category", "Planned", "Spent", "Remaining", "Progress"};

        // Sample data
        Object[][] data = {
//                {"Housing", 1500.00, 1500.00, 0.00, 100.0},
//                {"Food", 600.00, 450.75, 149.25, 75.1},
//                {"Transportation", 400.00, 325.50, 74.50, 81.4},
//                {"Utilities", 300.00, 285.25, 14.75, 95.1},
//                {"Entertainment", 200.00, 175.00, 25.00, 87.5},
//                {"Health", 300.00, 0.00, 300.00, 0.0},
//                {"Personal", 250.00, 50.00, 200.00, 20.0},
//                {"Savings", 250.00, 250.00, 0.00, 100.0}
        };

        // Create a custom table model
        budgetTableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column >= 1 && column <= 3) return Double.class;
                if (column == 4) return Double.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Only make the "Planned" column editable
            }
        };

        // Create and configure the table
        budgetTable = new JTable(budgetTableModel);
        budgetTable.setRowHeight(40);
        budgetTable.setShowVerticalLines(false);
        budgetTable.setGridColor(new Color(240, 240, 240));
        budgetTable.setFont(NORMAL_FONT);
        budgetTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        budgetTable.getTableHeader().setBackground(new Color(245, 245, 250));
        budgetTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        budgetTable.setSelectionBackground(new Color(240, 240, 250));

        // Set custom renderers for currency and progress columns
        budgetTable.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            DecimalFormat formatter = new DecimalFormat("$#,##0.00");

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value instanceof Double) {
                    double val = (Double) value;

                    if (column == 1 || column == 2 || column == 3) {
                        setText(formatter.format(val));
                        setHorizontalAlignment(JLabel.RIGHT);
                    } else if (column == 4) {
                        // Format progress as percentage
                        setText(String.format("%.1f%%", val));
                        setHorizontalAlignment(JLabel.CENTER);

                        // Color code progress
                        if (val >= 100) {
                            setForeground(KU_RED);
                        } else if (val >= 75) {
                            setForeground(new Color(255, 149, 0)); // Orange
                        } else {
                            setForeground(SUCCESS_COLOR);
                        }
                    }
                }

                return c;
            }
        });

        // Add the table to the panel
        JScrollPane tableScrollPane = new JScrollPane(budgetTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        budgetPanel.add(budgetHeaderPanel, BorderLayout.NORTH);
        budgetPanel.add(tableScrollPane, BorderLayout.CENTER);
    }

    private void createTransactionsPanel() {
        transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBackground(Color.WHITE);
        transactionsPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        transactionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));
        transactionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header section
        JPanel transactionsHeaderPanel = new JPanel(new BorderLayout());
        transactionsHeaderPanel.setBackground(Color.WHITE);

        JLabel transactionsTitle = new JLabel("Recent Transactions");
        transactionsTitle.setFont(HEADER_FONT);

        JButton viewAllButton = new JButton("View All");
        viewAllButton.setBackground(Color.WHITE);
        viewAllButton.setForeground(KU_BLUE);
        viewAllButton.setBorder(new LineBorder(KU_BLUE, 1, true));
        viewAllButton.setFocusPainted(false);

        transactionsHeaderPanel.add(transactionsTitle, BorderLayout.WEST);
        transactionsHeaderPanel.add(viewAllButton, BorderLayout.EAST);

        // Transaction list panel
        JPanel transactionListPanel = new JPanel();
        transactionListPanel.setLayout(new BoxLayout(transactionListPanel, BoxLayout.Y_AXIS));
        transactionListPanel.setBackground(Color.WHITE);

        // Sample transaction data
        String[][] transactions = {
//                {"March 20, 2025", "Grocery Store", "Food", "-$87.45"},
//                {"March 18, 2025", "Gas Station", "Transportation", "-$45.30"},
//                {"March 15, 2025", "Electricity Bill", "Utilities", "-$125.75"},
//                {"March 15, 2025", "Paycheck", "Income", "+$2,500.00"},
//                {"March 12, 2025", "Restaurant", "Food", "-$55.20"}
        };

        for (String[] transaction : transactions) {
            JPanel transactionPanel = createTransactionItem(transaction[0], transaction[1], transaction[2], transaction[3]);
            transactionListPanel.add(transactionPanel);
            transactionListPanel.add(Box.createRigidArea(new Dimension(0, 1)));
            transactionListPanel.add(new JSeparator());
            transactionListPanel.add(Box.createRigidArea(new Dimension(0, 1)));
        }

        // Add components to the transactions panel
        transactionsPanel.add(transactionsHeaderPanel, BorderLayout.NORTH);

        JScrollPane transactionsScrollPane = new JScrollPane(transactionListPanel);
        transactionsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        transactionsPanel.add(transactionsScrollPane, BorderLayout.CENTER);
    }

    private JPanel createTransactionItem(String date, String merchant, String category, String amount) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Left side - date and merchant
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);

        JLabel merchantLabel = new JLabel(merchant);
        merchantLabel.setFont(new Font("Arial", Font.BOLD, 14));
        merchantLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(merchantLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        leftPanel.add(dateLabel);

        // Right side - category and amount
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        amountLabel.setHorizontalAlignment(JLabel.RIGHT);
        amountLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Set color based on amount (red for negative, green for positive)
        if (amount.startsWith("-")) {
            amountLabel.setForeground(KU_RED);
        } else {
            amountLabel.setForeground(SUCCESS_COLOR);
        }

        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryLabel.setForeground(Color.GRAY);
        categoryLabel.setHorizontalAlignment(JLabel.RIGHT);
        categoryLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(amountLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        rightPanel.add(categoryLabel);

        // Add components to panel
        panel.add(leftPanel, BorderLayout.WEST);

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }


    private JPanel createSetting() {
        // Main panel with BorderLayout
        JPanel setting = new JPanel(new BorderLayout());

        // Scroll pane setup
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(KU_WHITE);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        setting.add(scrollPane, BorderLayout.CENTER);

        // Create two columns that will resize
        JPanel columnsContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        columnsContainer.setBackground(KU_WHITE);
        columnsContainer.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel firstColumn = new JPanel();
        firstColumn.setLayout(new BoxLayout(firstColumn, BoxLayout.Y_AXIS));
        firstColumn.setBackground(KU_WHITE);

        JPanel secondColumn = new JPanel();
        secondColumn.setLayout(new BoxLayout(secondColumn, BoxLayout.Y_AXIS));
        secondColumn.setBackground(KU_WHITE);

        columnsContainer.add(firstColumn);
        columnsContainer.add(secondColumn);
        contentPanel.add(columnsContainer);

        // Create setting cards with flexible sizing
        firstColumn.add(createSettingCard("Name", "Your name is how you get recognized"));
//    creates an invisible, fixed-height component used for adding vertical spacing between components in a container that uses
        firstColumn.add(Box.createVerticalStrut(20));
        firstColumn.add(createSettingCard("Phone", "Your phone number for account recovery"));
        firstColumn.add(Box.createVerticalStrut(20));
        firstColumn.add(createSettingCard("Password", "Change your account password"));

        secondColumn.add(createSettingCard("Email", "Your primary email address"));
        secondColumn.add(Box.createVerticalStrut(20));
        secondColumn.add(createSettingCard("Address", "Your physical mailing address"));

        return setting;
    }

    private JPanel createSettingCard(String title, String description) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(242, 242, 242));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(242, 242, 242));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(KU_GRAY);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 27));
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(KU_LIGHT_GRAY);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton actionButton = new JButton("Change " + title);
        actionButton.setForeground(KU_WHITE);
        actionButton.setBackground(KU_BLUE);
        actionButton.setFont(new Font("Arial", Font.BOLD, 15));
        actionButton.setBorder(new EmptyBorder(10, 10, 10, 10));
        actionButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(descLabel);
        contentPanel.add(actionButton);

        panel.add(contentPanel, BorderLayout.NORTH);
        panel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

        return panel;
    }





}
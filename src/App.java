import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.text.*;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;

public class App extends JFrame {
    private UUID userID;
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
    private JPanel transactionsViewPanel = new JPanel();
    private JPanel reportsPanel;
    private JPanel goalsPanel;
    private JPanel settingsPanel;

    // Model data


    private DefaultListModel<String> reportsListModel;
    private JList<String> reportsList;


    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

    // Budget table data
    private DefaultTableModel budgetTableModel;
    private JTable budgetTable;



    public App(User user) {
        userID = user.getUuid();
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
        dashboardPanel = createDashboardPanel(user);
        cardPanel.add(dashboardPanel, "Dashboard");

        // Create budget view panel
        budgetViewPanel = createBudgetViewPanel();
        cardPanel.add(budgetViewPanel, "Budget");

        // Create transactions view panel
        transactionsViewPanel = createTransactionsViewPanel(user);
        cardPanel.add(transactionsViewPanel, "Transactions");

        // Create other view panels (simplified for this example)
        reportsPanel = createPlaceholderPanel("Reports View");
        cardPanel.add(createReportsPanel(user), "Reports");

        goalsPanel = createPlaceholderPanel("Goals View");
        cardPanel.add(goalsPanel, "Goals");

        settingsPanel = createSetting();
        cardPanel.add(settingsPanel, "Settings");

        // Add header and card panel to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createDashboardPanel(User user) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(KU_WHITE);

        // Create summary cards panel
        createSummaryPanel();

        // Create budget categories panel
        createBudgetPanel();

        // Create recent transactions panel
        createTransactionsPanel(user.getUuid());

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

        addButton.addActionListener(e -> {showAddBudgetDialog(this, userID);});

        controlsPanel.add(addButton);
        panel.add(controlsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTransactionsViewPanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(KU_WHITE);

        // Create a more detailed transactions view
        JLabel title = new JLabel("Transaction History", JLabel.CENTER);
        title.setFont(HEADER_FONT);
        panel.add(title, BorderLayout.NORTH);

        // Add the transactions list from the original dashboard
        createTransactionsPanel(user.getUuid());
        panel.add(transactionsPanel, BorderLayout.CENTER);

        // Add some additional transaction controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlsPanel.setBackground(KU_WHITE);



        panel.add(controlsPanel, BorderLayout.SOUTH);

        return panel;
    }
    public void createTransactionDialog(JFrame parent, UUID userId) {
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
        JTextField dateField = new JTextField(12);
        dateField.setFont(NORMAL_FONT);
        panel.add(dateLabel, gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(NORMAL_FONT);
        categoryLabel.setForeground(KU_GRAY);
        JComboBox<String> categoryField = new JComboBox<>(new String[]{
                "Food & Dining",
                "Shopping",
                "Transportation",
                "Housing & Rent",
                "Utilities",
                "Health & Medical",
                "Entertainment",
                "Education",
                "Savings & Investments",
                "Personal Care",
                "Other"
        });
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
                String category = categoryField.getSelectedItem().toString();
                String source = sourceField.getText();
                String description = descField.getText();

                Transaction tx = new Transaction(userId, type, amount, date, category, source, description);
                Transaction.addTransaction(tx);
                refreshTransactionList(userId);

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
        double totalIncome = Transaction.getTotalIncome(userID);
        double plannedExpenses = Budget.planedForUser(userID);
        double actualExpenses = Transaction.getTotalExpenses(userID);

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

    private void openSelectedReport(User user) {
        String selected = reportsList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a report to open.");
            return;
        }
        try {
            File reportFile = new File("reports/" + user.getUuid() + "/" + selected);
            if (!reportFile.exists()) {
                JOptionPane.showMessageDialog(this, "File not found.");
                return;
            }
            Desktop.getDesktop().open(reportFile);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to open report: " + e.getMessage());
        }
    }




    private JPanel createReportsPanel(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top: Generate button
        JButton generateBtn = new JButton("Generate New Report");
        generateBtn.setBackground(new Color(0, 123, 255));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFont(new Font("Arial", Font.BOLD, 14));
        generateBtn.setFocusPainted(false);

        generateBtn.addActionListener(e -> {
            user.progressReport();  // 🔥 existing method to generate the report
            refreshReportsList(user); // Refresh the report list
        });




        panel.add(generateBtn, BorderLayout.NORTH);

        // Center: List of reports
        reportsListModel = new DefaultListModel<>();
        reportsList = new JList<>(reportsListModel);
        reportsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportsList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(reportsList);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom: Open button
        JButton openBtn = new JButton("Open Report");
        openBtn.setBackground(new Color(40, 167, 69));
        openBtn.setForeground(Color.WHITE);
        openBtn.setFont(new Font("Arial", Font.BOLD, 14));
        openBtn.setFocusPainted(false);

        openBtn.addActionListener(e -> openSelectedReport(user));
        panel.add(openBtn, BorderLayout.SOUTH);

        refreshReportsList(user); // Load initially

        return panel;
    }

    private void refreshReportsList(User user) {
        reportsListModel.clear();
        File reportsDir = new File("reports/" + user.getUuid()); // Assuming reports are saved per user in a folder
        if (reportsDir.exists() && reportsDir.isDirectory()) {
            for (File report : reportsDir.listFiles()) {
                if (report.getName().endsWith(".txt")) {
                    reportsListModel.addElement(report.getName());
                }
            }
        }
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
        String[] columnNames = {
                "Category", "Planned", "Spent", "Remaining", "Progress", "Status", "Approval"
        };


        ArrayList<Budget> budgets = Budget.getBudgetsForUser(userID);
        List<Object[]> rowData = new ArrayList<>();



        for (Budget b : budgets) {
            double progress = b.getSetAmount() > 0 ? (b.getActualSpent() / b.getSetAmount()) * 100 : 0;
            rowData.add(new Object[]{
                    b.getCategory(),
                    b.getSetAmount(),
                    b.getActualSpent(),
                    b.getRemainingAmount(),
                    progress,
                    b.getStatus(),
                    b.getApprovalStatus()
            });
        }
        Object[][] data = rowData.toArray(new Object[0][]);


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
                return column == 1 || column == 0; // Only make the "Planned" column editable
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

        budgetTableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();

            if (col == 1 || col == 0) {  // Planned column
                String category = (String) budgetTableModel.getValueAt(row, 0);
                Object newValue = budgetTableModel.getValueAt(row, 1);

                try {
                    double newPlannedAmount = Double.parseDouble(newValue.toString());

                    // Find the corresponding Budget object
                    for (Budget b : budgets) {
                        if (b.getCategory().equals(category)) {
                            b.updateInDb(category, newPlannedAmount);
                            JOptionPane.showMessageDialog(budgetPanel, "Budget updated for category: " + category);
                            break;
                        }
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(budgetPanel, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    public static void showAddBudgetDialog(JFrame parent, UUID userId) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(KU_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel title = new JLabel("Create New Budget");
        title.setFont(HEADER_FONT);
        title.setForeground(KU_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        // Category
        gbc.gridy++;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(NORMAL_FONT);
        categoryLabel.setForeground(KU_GRAY);
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{
                "Food & Dining",
                "Shopping",
                "Transportation",
                "Housing & Rent",
                "Utilities",
                "Health & Medical",
                "Entertainment",
                "Education",
                "Savings & Investments",
                "Personal Care",
                "Other"
        });
        categoryBox.setFont(NORMAL_FONT);
        categoryBox.setBackground(KU_LIGHT_BLUE);
        categoryBox.setForeground(KU_WHITE);
        categoryBox.setFocusable(false);
        panel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        panel.add(categoryBox, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel amountLabel = new JLabel("Budget Amount:");
        amountLabel.setFont(NORMAL_FONT);
        amountLabel.setForeground(KU_GRAY);
        JTextField amountField = new JTextField(12);
        amountField.setFont(NORMAL_FONT);
        panel.add(amountLabel, gbc);
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        // Show dialog
        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                "Add Budget",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                String category = categoryBox.getSelectedItem().toString();
                double amount = Double.parseDouble(amountField.getText());

                Budget newBudget = new Budget(userId, category, amount);
                Budget.addBudget(newBudget);

                JOptionPane.showMessageDialog(parent,
                        "Budget added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent,
                        "Failed to add budget:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void createTransactionsPanel(UUID userId) {
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

        // Load transactions from DB
        ArrayList<Transaction> transactions = Transaction.getTransactions(userId);
        for (Transaction tx : transactions) {


            JPanel transactionPanel = createTransactionItem(tx);
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

    private JPanel createTransactionItem(Transaction tx) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Left side
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);

        JLabel merchantLabel = new JLabel(tx.getType().equals("income") ? tx.getSource() : tx.getDescription());
        merchantLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel dateLabel = new JLabel(tx.getDate().toString());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);

        leftPanel.add(merchantLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        leftPanel.add(dateLabel);

        // Right side
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        String formattedAmount = (tx.getType().equalsIgnoreCase("income") ? "+" : "-") + "$" + String.format("%.2f", tx.getAmount());
        JLabel amountLabel = new JLabel(formattedAmount);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        amountLabel.setForeground(tx.getType().equalsIgnoreCase("income") ? SUCCESS_COLOR : KU_RED);

        JLabel categoryLabel = new JLabel(tx.getCategory());
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryLabel.setForeground(Color.GRAY);

        rightPanel.add(amountLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        rightPanel.add(categoryLabel);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        // Add mouse listener for editing
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 || SwingUtilities.isRightMouseButton(e)) {
                    openEditTransactionDialog(tx);
                }
            }
        });

        return panel;
    }

    private void openEditTransactionDialog(Transaction tx) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(Color.WHITE);

        JTextField amountField = new JTextField(String.valueOf(tx.getAmount()));
        JTextField dateField = new JTextField(tx.getDate().toString());
        JTextField categoryField = new JTextField(tx.getCategory());
        JTextField descriptionField = new JTextField(tx.getDescription());
        JTextField sourceField = new JTextField(tx.getSource());
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"income", "expense"});
        typeBox.setSelectedItem(tx.getType().toLowerCase());

        panel.add(new JLabel("Type:"));
        panel.add(typeBox);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Source:"));
        panel.add(sourceField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Transaction",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                tx.setType(typeBox.getSelectedItem().toString());
                tx.setAmount(Double.parseDouble(amountField.getText()));
                tx.setDate(Date.valueOf(dateField.getText()));
                tx.setCategory(categoryField.getText());
                tx.setSource(sourceField.getText());
                tx.setDescription(descriptionField.getText());

                tx.updateInDB();  // ✅ your existing DB update function

                JOptionPane.showMessageDialog(null, "Transaction updated!");
                refreshTransactionList(tx.getUserId());  // 🔁 Reload UI
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid input: " + ex.getMessage());
            }
        }
    }

    private void refreshTransactionList(UUID userId) {
        transactionsPanel.removeAll();

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

        JPanel transactionListPanel = new JPanel();
        transactionListPanel.setLayout(new BoxLayout(transactionListPanel, BoxLayout.Y_AXIS));
        transactionListPanel.setBackground(Color.WHITE);

        ArrayList<Transaction> transactions = Transaction.getTransactions(userId);
        for (Transaction tx : transactions) {
            JPanel transactionPanel = createTransactionItem(tx);
            transactionListPanel.add(transactionPanel);
            transactionListPanel.add(Box.createRigidArea(new Dimension(0, 1)));
            transactionListPanel.add(new JSeparator());
            transactionListPanel.add(Box.createRigidArea(new Dimension(0, 1)));
        }

        JScrollPane transactionsScrollPane = new JScrollPane(transactionListPanel);
        transactionsScrollPane.setBorder(BorderFactory.createEmptyBorder());

        transactionsPanel.add(transactionsHeaderPanel, BorderLayout.NORTH);
        transactionsPanel.add(transactionsScrollPane, BorderLayout.CENTER);

        transactionsPanel.revalidate();
        transactionsPanel.repaint();
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
        actionButton.addActionListener(e -> showUpdateDialog(null));

        return panel;
    }





    public void showUpdateDialog(Component parent) {
        try {
            // Load the current user object from DB
            User currentUser = new User(userID);  // assumes constructor loads from DB

            // Create input fields and pre-fill with existing values
            JTextField nameField = new JTextField(currentUser.getName());
            JTextField emailField = new JTextField(currentUser.getEmail());
            JTextField addressField = new JTextField(currentUser.getAddress());
            JTextField phoneField = new JTextField(currentUser.getPhoneNum());
            JPasswordField passwordField = new JPasswordField(currentUser.getPassword());  // pre-fill password

            // Create panel with input layout
            JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Address:"));
            panel.add(addressField);
            panel.add(new JLabel("Phone:"));
            panel.add(phoneField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);

            int result = JOptionPane.showConfirmDialog(
                    parent,
                    panel,
                    "Update User Data",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                // Read values
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);

                Arrays.fill(passwordChars, ' '); // clear password from memory

                // Validate
                if (name.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                    throw new IllegalArgumentException("All fields must be filled");
                }

                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    throw new IllegalArgumentException("Invalid email format");
                }

                if (password.isEmpty()) {
                    throw new IllegalArgumentException("Password cannot be empty");
                }

                // Update user
                currentUser.setName(name);
                currentUser.setEmail(email);
                currentUser.setAddress(address);
                currentUser.setPhoneNum(phone);
                currentUser.setPassword(password);

                currentUser.updateInDB();
                JOptionPane.showMessageDialog(parent, "Update successful!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Failed to load or update user: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }









/*
    private void handleSettingChange(String settingType) {
        switch (settingType) {
            case "Email":
                changeEmail();
                break;
            case "Password":
                changePassword();
                break;
            // Add cases for other settings if needed
        }
    }

    private void changeEmail() {
        String newEmail = JOptionPane.showInputDialog(null, "Enter new email:");
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            if (isValidEmail(newEmail)) {
                updateEmailInDatabase(newEmail);
            } else {
                JOptionPane.showMessageDialog(panel, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private boolean isValidEmail(String email) {
            return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        }

        
private void changePassword() {
    JPasswordField currentPassField = new JPasswordField();
    JPasswordField newPassField = new JPasswordField();
    JPasswordField confirmPassField = new JPasswordField();

    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("Current Password:"));
    panel.add(currentPassField);
    panel.add(new JLabel("New Password:"));
    panel.add(newPassField);
    panel.add(new JLabel("Confirm New Password:"));
    panel.add(confirmPassField);

    int result = JOptionPane.showConfirmDialog(
        null, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION);

    if (result == JOptionPane.OK_OPTION) {
        char[] newPass = newPassField.getPassword();
        char[] confirmPass = confirmPassField.getPassword();
        
        if (Arrays.equals(newPass, confirmPass)) {
            updatePasswordInDatabase(new String(newPass));
        } else {
            JOptionPane.showMessageDialog(panel, "Passwords don't match", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

private boolean isValidEmail(String email) {
    return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
}

private void updateEmailInDatabase(String newEmail) {
    try (Connection connection = NeonDB.getConnection()) {
        String sql = "UPDATE users SET email = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newEmail);
            pstmt.setInt(2, currentUserId); // You need to have current user ID
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Email updated successfully!");
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error updating email: " + ex.getMessage(), 
            "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void updatePasswordInDatabase(String newPassword) {
    try (Connection connection = NeonDB.getConnection()) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword); // Plain text storage (INSECURE)
            pstmt.setInt(2, currentUserId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Password updated successfully!");
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error updating password: " + ex.getMessage(), 
            "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
 */


}
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.UUID;

public class Dashboard extends JFrame {
    private static final Color DARK_BG = new Color(30, 30, 36);
    private static final Color CARD_BG = new Color(46, 46, 54);
    private static final Color ACCENT = new Color(0, 230, 161);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 240);

    public Dashboard(User user) {
        JFrame frame = new JFrame("Dark Budget Dashboard");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(DARK_BG);

        // Create sidebar with hover effects
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 800));
        sidebar.setBackground(new Color(40, 40, 46));
        sidebar.setLayout(new GridLayout(8, 1, 0, 15));
        sidebar.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        String[] menuItems = {"Dashboard", "Income", "Expenses", "Budgets", "Reports", "Settings", "Help", "Logout"};
        for (String item : menuItems) {
            JButton btn = createSidebarButton(item);
            sidebar.add(btn);
        }

        // Main content area
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(DARK_BG);
        mainContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DARK_BG);
        JLabel title = new JLabel("Welcome Back " + user.getName() );
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);
        JLabel date = new JLabel("April 2025");
        date.setFont(new Font("Arial", Font.PLAIN, 16));
        date.setForeground(new Color(180, 180, 180));
        header.add(title, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);

        // Summary cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        cardsPanel.setBackground(DARK_BG);
        cardsPanel.add(createSummaryCard("Total Income", "$5,000", ACCENT));
        cardsPanel.add(createSummaryCard("Total Expenses", "$3,200", new Color(255, 85, 85)));
        cardsPanel.add(createSummaryCard("Remaining", "$1,800", new Color(255, 184, 77)));

        // Recent transactions table
        String[] columnNames = {"Date", "Category/source", "Amount", "Description", };
        ArrayList<Transaction> transactionList = Transaction.getTransactions(user.getUuid());
        System.out.println(transactionList);
        Object[][] data = transactionList.toArray(new Object[transactionList.size()][]);


        JTable transactionsTable = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        styleTable(transactionsTable);
        JScrollPane tableScroll = new JScrollPane(transactionsTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.setBackground(DARK_BG);

        // Add components to main content
        mainContent.add(header, BorderLayout.NORTH);
        mainContent.add(cardsPanel, BorderLayout.CENTER);
        mainContent.add(tableScroll, BorderLayout.SOUTH);

        // Floating action button
        JButton addButton = new JButton("+ Add Transaction");
        addButton.setBackground(ACCENT);
        addButton.setForeground(DARK_BG);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        addButton.addActionListener(e -> addTransactionInput(frame, user));

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(mainContent, BorderLayout.CENTER);
        frame.add(addButton, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(new Color(40, 40, 46));
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(60, 60, 66));
                btn.setForeground(ACCENT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(40, 40, 46));
                btn.setForeground(TEXT_PRIMARY);
            }
        });

        return btn;
    }

    private static JPanel createSummaryCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(200, 200, 200));
        gbc.gridy = 0;
        card.add(titleLabel, gbc);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 1;
        card.add(valueLabel, gbc);

        return card;
    }

    private static void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 5));
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        table.getTableHeader().setBackground(new Color(60, 60, 66));
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(DARK_BG);
    }

    private static void addTransactionInput(JFrame parent, User user) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Type selection
        String[] type = {"Income", "Expense"};
        JComboBox<String> typeSelect = new JComboBox<>(type);
        styleComponent(typeSelect);

        // Amount field
        JTextField amountField = new JTextField();
        styleComponent(amountField);

        // Date field with placeholder behavior
        JTextField dateField = new JTextField();
        styleComponent(dateField);
        // Set up the placeholder text and gray color initially.
        dateField.setForeground(Color.GRAY);
        dateField.setText("YYYY-MM-DD");
        dateField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (dateField.getText().equals("YYYY-MM-DD")) {
                    dateField.setText("");
                    dateField.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (dateField.getText().trim().isEmpty()) {
                    dateField.setText("YYYY-MM-DD");
                    dateField.setForeground(Color.GRAY);
                }
            }
        });

        // Category field
        JTextField category = new JTextField();
        styleComponent(category);

        // Source field
        JTextField source = new JTextField();
        styleComponent(source);

        // Description field
        JTextField descField = new JTextField();
        styleComponent(descField);

        // Define behavior based on transaction type selection.
        // Initially default is "Income": disable category, enable source.
        category.setEnabled(false);
        source.setEnabled(true);
        typeSelect.addActionListener(e -> {
            String selected = (String) typeSelect.getSelectedItem();
            if ("Income".equals(selected)) {
                category.setEnabled(false);
                source.setEnabled(true);
            } else if ("Expense".equals(selected)) {
                category.setEnabled(true);
                source.setEnabled(false);
            }
        });

        // Add fields to panel using a helper method.
        addField(panel, gbc, "Type:", typeSelect);
        addField(panel, gbc, "Amount:", amountField);
        addField(panel, gbc, "Date:", dateField);
        addField(panel, gbc, "Category:", category);
        addField(panel, gbc, "Source:", source);
        addField(panel, gbc, "Description:", descField);

        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                "Add New Transaction",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                // If the date field still shows the placeholder, treat it as empty.
                String dateText = dateField.getText().equals("YYYY-MM-DD") ? "" : dateField.getText();
                Transaction tx = new Transaction(
                        user.getUuid(),
                        typeSelect.getSelectedItem().toString(),
                        Double.parseDouble(amountField.getText()),
                        Date.valueOf(dateText),
                        category.getText(),
                        source.getText(),
                        descField.getText()
                );
                Transaction.addTransaction(tx);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parent, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(parent, "Invalid Date", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private static void addField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent inputField) {
        gbc.gridx = 0;
        panel.add(createLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(inputField, gbc);
        gbc.gridy++;
    }

    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_PRIMARY);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }


    private static void styleComponent(JComponent component) {
        component.setBackground(CARD_BG);
        component.setForeground(TEXT_PRIMARY);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 86)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        if (component instanceof JComboBox) {
            ((JComboBox<?>) component).setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setBackground(CARD_BG);
                    setForeground(TEXT_PRIMARY);
                    return this;
                }
            });
        }
    }
}
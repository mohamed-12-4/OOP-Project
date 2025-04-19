//import javax.swing.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class AdminMainPage extends JFrame implements ActionListener {
//
//
//
//    public AdminMainPage(){
//
//    }
//
//    public static void main(String args[]){
//
//    }
//
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//
//    }
//}
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {

    // Sample data storage
    private List<User> users = new ArrayList<>();
    private JTable userTable;
    private DefaultTableModel tableModel;

    public AdminDashboard() {
        setTitle("Admin Dashboard - Budget Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize sample data


        // Create the main panel with a BorderLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Add components to the main panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createSidebarPanel(), BorderLayout.WEST);
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);

        // Add the main panel to the frame
        add(mainPanel);

        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(40, 58, 82)); // Dark blue
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        headerPanel.add(titleLabel);

        return headerPanel;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(50, 70, 100)); // Darker blue
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        // Add sidebar buttons
        String[] menuItems = {"Dashboard", "Users", "Budgets", "Reports", "Settings"};
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(50, 70, 100));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.PLAIN, 16));
            button.setPreferredSize(new Dimension(200, 40));
            button.setMaximumSize(new Dimension(200, 40));
            sidebarPanel.add(button);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        return sidebarPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        // Add a card for summary
        JPanel summaryCard = createSummaryCard();
        contentPanel.add(summaryCard, BorderLayout.NORTH);

        // Add the user table
        JPanel tablePanel = createUserTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        // Add action buttons
        JPanel actionPanel = createActionPanel();
        contentPanel.add(actionPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    private JPanel createSummaryCard() {
        JPanel summaryCard = new JPanel();
        summaryCard.setBackground(Color.WHITE);
        summaryCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        summaryCard.setLayout(new GridLayout(1, 3, 10, 10));

        // Add summary items
        String[] summaries = {"Total Users: 100", "Pending Budgets: 10", "Approved Budgets: 90"};
        for (String summary : summaries) {
            JLabel label = new JLabel(summary, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 18));
            label.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            label.setOpaque(true);
            label.setBackground(new Color(240, 240, 240));
            summaryCard.add(label);
        }

        return summaryCard;
    }

    private JPanel createUserTablePanel() {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());

        // Column names for the table
        String[] columnNames = {"User ID", "Name", "Email", "Income", "Expenses", "Budget Status"};

        // Table model
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        userTable.setFillsViewportHeight(true);
        userTable.setRowHeight(30);
        userTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Populate table with sample data
        for (User user : users) {
            Object[] rowData = {
                    user.getId(),
                    user.getName(),
                    user.getEmail(),

                    user.getExpenses(),

            };
            tableModel.addRow(rowData);
        }

        JScrollPane scrollPane = new JScrollPane(userTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Buttons for actions
        JButton approveButton = createButton("Approve", new Color(40, 167, 69)); // Green
        JButton rejectButton = createButton("Reject", new Color(220, 53, 69)); // Red
        JButton editButton = createButton("Edit", new Color(255, 193, 7)); // Yellow
        JButton deleteButton = createButton("Delete", new Color(108, 117, 125)); // Gray
        JButton searchButton = createButton("Search", new Color(0, 123, 255)); // Blue

        // Add action listeners
        approveButton.addActionListener(e -> approveBudget());
        rejectButton.addActionListener(e -> rejectBudget());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        searchButton.addActionListener(e -> searchUser());

        // Add buttons to the panel
        actionPanel.add(approveButton);
        actionPanel.add(rejectButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(searchButton);

        return actionPanel;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 40));
        return button;
    }

    private void approveBudget() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            users.get(selectedRow).setBudgetStatus("Approved");
            tableModel.setValueAt("Approved", selectedRow, 5);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to approve.");
        }
    }

    private void rejectBudget() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            users.get(selectedRow).setBudgetStatus("Rejected");
            tableModel.setValueAt("Rejected", selectedRow, 5);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to reject.");
        }
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            User user = users.get(selectedRow);
            String newName = JOptionPane.showInputDialog(this, "Edit Name:", user.getName());
            String newEmail = JOptionPane.showInputDialog(this, "Edit Email:", user.getEmail());
            if (newName != null && newEmail != null) {
                user.setName(newName);
                user.setEmail(newEmail);
                tableModel.setValueAt(newName, selectedRow, 1);
                tableModel.setValueAt(newEmail, selectedRow, 2);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            users.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }

    private void searchUser() {
        String searchTerm = JOptionPane.showInputDialog(this, "Enter name or budget status:");
        if (searchTerm != null) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String name = (String) tableModel.getValueAt(i, 1);
                String status = (String) tableModel.getValueAt(i, 5);
                if (name.contains(searchTerm) || status.contains(searchTerm)) {
                    userTable.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard());
    }
}


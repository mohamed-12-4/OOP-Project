import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUp extends JFrame implements ActionListener {
    // Constants for frame dimensions and colors
    private final static int WIDTH = 1000, HEIGHT = 600; // Frame dimensions
    private static final Color KU_BLUE = new Color(0, 84, 150); // KU Blue color
    private static final Color KU_WHITE = new Color(255, 255, 255); // White color
    private static final Color KU_GRAY = new Color(62, 62, 62); // Gray color
    private static final Color KU_LIGHT_GRAY = new Color(102, 102, 102); // Light gray color
    private static final Color KU_LIGHT_BLUE = new Color(91, 128, 178); // Light blue color
    private static final Color KU_RED = new Color(255,0,0);

    // UI Components
    private JPanel middlePanel, sidePanels, error; // Panels for right section
    private JLabel signLabel, nameLabel, emailLabel, phoneLabel, addressLabel, passwordLabel, signInLabel; // Labels
    private JTextField nameInput, emailInput, phoneInput, addressInput;
    private JPasswordField passwordInput; // Password field
    private JButton showPasswordToggle, signInButton, signUpButton; // Buttons for sign-in and sign-up
    private boolean isTicked = true;

    public SignUp() {
        setTitle("Sign Up - KU Budget"); // Title of the frame
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Close operation
        setSize(WIDTH, HEIGHT); // Set frame size
        setResizable(false); // Disable resizing

        // Main panel to hold the right panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS)); // Horizontal layout

        rightPanel(); // Create the right panel

        sidePanels();
        mainPanel.add(sidePanels);
        // Add the right panel to the main panel
        mainPanel.add(middlePanel);
        sidePanels();

        mainPanel.add(sidePanels);
        // Add main panel to the frame and make it visible
        add(mainPanel);
        setVisible(true);
    }

    private void sidePanels(){
        sidePanels = new JPanel();
        sidePanels.setPreferredSize(new Dimension((int) (0.2f * WIDTH), HEIGHT)); // 60% of the width
        sidePanels.setLayout(new BorderLayout()); // Border layout for the right panel
        sidePanels.setBackground(KU_LIGHT_BLUE); // Set background color
    }

    private void rightPanel() {
        middlePanel = new JPanel();
        middlePanel.setPreferredSize(new Dimension((int) (0.6f * WIDTH), HEIGHT)); // 60% of the width
        middlePanel.setLayout(new BorderLayout()); // Border layout for the right panel
        middlePanel.setBackground(KU_WHITE); // Set background color

        // Main content panel with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Vertical layout
        contentPanel.setBackground(KU_WHITE); // Set background color
        contentPanel.setBorder(new EmptyBorder(60, 80, 60, 80)); // Add padding

        // Sign Up Label
        signLabel = new JLabel("Sign Up for a New Account");
        signLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the label
        signLabel.setFont(new Font("Arial", Font.BOLD, 22)); // Set font and size
        signLabel.setForeground(KU_GRAY); // Set text color
        signLabel.setBorder(new EmptyBorder(10, 0, 30, 0)); // Add padding

        // Form Panel: Contains name, email, phone, address, password, and sign-up button
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS)); // Vertical layout
        formPanel.setBackground(KU_WHITE); // Set background color
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the form panel

        JPanel emptySmallBetween1 = new JPanel();
        emptySmallBetween1.setBackground(KU_WHITE); // Set background color
        emptySmallBetween1.setMaximumSize(new Dimension(100, 20)); // Set maximum size

        JPanel emptySmallBetween2 = new JPanel();
        emptySmallBetween2.setBackground(KU_WHITE); // Set background color
        emptySmallBetween2.setMaximumSize(new Dimension(100, 20)); // Set maximum size

        JPanel emptySmallBetween3 = new JPanel();
        emptySmallBetween3.setBackground(KU_WHITE); // Set background color
        emptySmallBetween3.setMaximumSize(new Dimension(100, 20)); // Set maximum size

        JPanel emptySmallBetween4 = new JPanel();
        emptySmallBetween4.setBackground(KU_WHITE); // Set background color
        emptySmallBetween4.setMaximumSize(new Dimension(100, 20)); // Set maximum size

        JPanel emptySmallBetween5 = new JPanel();
        emptySmallBetween5.setBackground(KU_WHITE); // Set background color
        emptySmallBetween5.setMaximumSize(new Dimension(100, 20)); // Set maximum size

        // Name Panel
        JPanel namePanel = createFormFieldPanel("Name: ", "Name");
        formPanel.add(namePanel);
        formPanel.add(emptySmallBetween1);

        // Email Panel
        JPanel emailPanel = createFormFieldPanel("Email: ", "Email Address");
        formPanel.add(emailPanel);
        formPanel.add(emptySmallBetween2);

        // Phone Panel
        JPanel phonePanel = createFormFieldPanel("Phone: ", "Phone");
        formPanel.add(phonePanel);
        formPanel.add(emptySmallBetween3);

        // Address Panel
        JPanel addressPanel = createFormFieldPanel("Address: ", "Address");
        formPanel.add(addressPanel);
        formPanel.add(emptySmallBetween4);

        // Password Panel
        JPanel passwordPanel = createPasswordPanel();
        formPanel.add(passwordPanel);



        //error panel
        error = new JPanel();
        error.setLayout(new BoxLayout(error,BoxLayout.Y_AXIS));
        error.setBackground(KU_WHITE);
        error.setMaximumSize(new Dimension(400,50));
        error.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the panel
        error.setVisible(false);

        JLabel errorMessage = new JLabel("Please enter correct credentials. Password is case-sensitive.");
        errorMessage.setForeground(KU_RED);
        errorMessage.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font and size
        error.add(errorMessage);


        // Add spacing before the button
        formPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add vertical spacing

        // Sign Up Button
        signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 25)); // Set font and size
        signUpButton.setForeground(KU_WHITE); // Set text color
        signUpButton.setBackground(KU_BLUE); // Set background color
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
        signUpButton.setMaximumSize(new Dimension(400, 50)); // Set maximum size
        signUpButton.addActionListener(this);
        formPanel.add(signUpButton);
        formPanel.add(emptySmallBetween5);



        // Sign In Prompt Panel
        JPanel signUpPromptPanel = new JPanel();
        signUpPromptPanel.setLayout(new BoxLayout(signUpPromptPanel, BoxLayout.X_AXIS)); // Horizontal layout
        signUpPromptPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the panel
        signUpPromptPanel.setBackground(KU_WHITE); // Set background color
        signUpPromptPanel.setMaximumSize(new Dimension(400, 30)); // Set maximum size

        signInLabel = new JLabel("Already have an account?");
        signInLabel.setForeground(KU_LIGHT_BLUE); // Set text color
        signInLabel.setBackground(KU_WHITE); // Set background color
        signInLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align label to the left

        signInButton = new JButton("Sign In");
        signInButton.setForeground(KU_WHITE); // Set text color
        signInButton.setBackground(KU_BLUE); // Set background color
        signInButton.addActionListener(this);
        signUpPromptPanel.add(signInLabel); // Add label
        signUpPromptPanel.add(Box.createHorizontalGlue()); // Add glue to push the button to the right
        signUpPromptPanel.add(signInButton); // Add button


        formPanel.add(error);

        // Add spacing before the sign-in prompt
        formPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add vertical spacing
//        formPanel.add(signUpPromptPanel);

        // Add components to the content panel
        contentPanel.add(signLabel, "North"); // Add sign-up label
        contentPanel.add(formPanel); // Add form panel
        contentPanel.add(signUpPromptPanel,"South");
        // Add content panel to the right panel
        middlePanel.add(contentPanel, BorderLayout.CENTER); // Add content panel to the center
    }

    private JPanel createPasswordPanel() {
        JPanel passwordPanel = new JPanel(new BorderLayout()); // Border layout for password panel
        passwordPanel.setBackground(KU_WHITE); // Set background color
        passwordPanel.setMaximumSize(new Dimension(400, 50)); // Set maximum size

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Set font and size
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Add padding

        passwordInput = new JPasswordField("Password");
        passwordInput.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font and size
        passwordInput.setForeground(Color.GRAY); // Set text color
        passwordInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Add focus listener for placeholder behavior
        passwordInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(passwordInput.getPassword()).equals("Password")) {
                    passwordInput.setText("");
                    passwordInput.setEchoChar('•');
                    passwordInput.setForeground(KU_GRAY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (new String(passwordInput.getPassword()).isEmpty()) {
                    passwordInput.setText("Password");
                    passwordInput.setEchoChar((char) 0);
                    passwordInput.setForeground(Color.GRAY);
                }
            }
        });

        // Password toggle button
        showPasswordToggle = new JButton("□");
        showPasswordToggle.setBackground(KU_WHITE);
        showPasswordToggle.setBorder(BorderFactory.createLineBorder(KU_WHITE, 1)); // Add border
        showPasswordToggle.setBorder(new EmptyBorder(0,0,0,20));
        showPasswordToggle.addActionListener(this);

        passwordPanel.add(passwordLabel, BorderLayout.WEST); // Add label to the left
        passwordPanel.add(passwordInput, BorderLayout.CENTER); // Add text field to the center
        passwordPanel.add(showPasswordToggle, BorderLayout.EAST);
        passwordPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, KU_GRAY), // Add border
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        return passwordPanel;
    }
    // Helper method to create a form field panel with improved placeholder behavior
    private JPanel createFormFieldPanel(String labelText, String placeholder) {
        JPanel panel = new JPanel(new BorderLayout()); // Border layout for the panel
        panel.setBackground(KU_WHITE); // Set background color
        panel.setMaximumSize(new Dimension(400, 50)); // Set maximum size

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 20)); // Set font and size
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Add padding

        // Create text field based on label
        JTextField textField;
        if (labelText.equals("Password: ")) {
            textField = new JPasswordField();
        } else {
            textField = new JTextField();
        }

        textField.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font and size
        textField.setForeground(Color.GRAY); // Set initial text color
        textField.setText(placeholder); // Set placeholder text
        textField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

        // Add focus listener to handle placeholder behavior
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(KU_GRAY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });

        // Store the text field as a class variable based on label
        switch (labelText) {
            case "Name: ":
                nameInput = textField;
                break;
            case "Email: ":
                emailInput = textField;
                break;
            case "Phone: ":
                phoneInput = textField;
                break;
            case "Address: ":
                addressInput = textField;
                break;
            case "Password: ":
                passwordInput = (JPasswordField) textField;
                break;
        }

        panel.add(label, BorderLayout.WEST); // Add label to the left
        panel.add(textField, BorderLayout.CENTER); // Add text field to the center
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, KU_GRAY), // Add border
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        return panel;
    }

    public static void main(String[] args) {
        // Use SwingUtilities to ensure thread safety for Swing components
        SwingUtilities.invokeLater(() -> new SignUp());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() ==showPasswordToggle){
            isTicked = !isTicked; // Toggle the state
            if (isTicked) {
                showPasswordToggle.setText("□"); // Set tick (Unicode)
                passwordInput.setEchoChar('•'); // Hide password (set echo character to bullet)

            } else {
                showPasswordToggle.setText("👁"); // Set empty box (Unicode)
                passwordInput.setEchoChar((char) 0); // Show password

            }
        }

        if (e.getSource() == signInButton) {

            new SignIn();
            this.dispose(); // Close the current window

        }


        if (e.getSource() == signUpButton) {
            String email = emailInput.getText();
            String name = nameInput.getText();
            String address = addressInput.getText();
            String phone = phoneInput.getText();
            String password = passwordInput.getText();
            if((email.trim().isEmpty() || email.equals("Email Address")||email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) || (name.trim().isEmpty()|| name.equals("Name")) || (address.trim().isEmpty() || address.equals("Address"))|| (phone.trim().isEmpty()|| phone.equals("Phone")||phone.matches("\\d{10,13}")) || (password.trim().isEmpty() || password.equals("Password")||(password.length() < 8))) {
                error.setVisible(true);
            }
            else {
                String query = "INSERT INTO users (name, email, phone, address, password, type) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 'user')";
                try (Connection conn = NeonDBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(query)) {


                    ps.setString(1, name);
                    ps.setString(2, email);
                    ps.setString(3, phone);
                    ps.setString(4,  address);
                    ps.setString(5, password);
                    ps.execute();


                } catch (SQLException err) {
                    err.printStackTrace();
                }
            }
        }
    }
}
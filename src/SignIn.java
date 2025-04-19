import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.*;
import java.util.UUID;

public class SignIn extends JFrame implements ActionListener {
    // Constants for frame dimensions and colors
    private final static int WIDTH = 1000, HEIGHT = 600; // Frame dimensions
    private static final Color KU_BLUE = new Color(0, 84, 150); // KU Blue color
    private static final Color KU_WHITE = new Color(255, 255, 255); // White color
    private static final Color KU_GRAY = new Color(62, 62, 62); // Gray color
    private static final Color KU_LIGHT_GRAY = new Color(102, 102, 102); // Light gray color
    private static final Color KU_RED = new Color(255,0,0);
    // UI Components
    private JPanel leftPanel, rightPanel, error; // Panels for left and right sections
    private JLabel signLabel, emailLabel, passwordLabel, titleLabel, empty, signUpLabel, welcomeLabel, appNameLabel; // Labels
    private JLabel uniLogo; // University logo
    private JPasswordField passwordInput;
    private JTextField emailInput; // Text fields for email and password
    private JButton showPasswordToggle, signInButton, signUpButton; // Buttons for sign-in and sign-up
    private boolean isTicked = true;

    // Constructor
    public SignIn() {
        // Set up the frame
        setTitle("Sign In - KU Budget"); // Title of the frame
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Close operation
        setSize(WIDTH, HEIGHT); // Set frame size
        setResizable(true); // Disable resizing

        // Main panel to hold left and right panels
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS)); // Horizontal layout

        // Initialize left and right panels
        leftPanel(); // Create the left panel
        rightPanel(); // Create the right panel

        // Add left and right panels to the main panel
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        // Add main panel to the frame and make it visible
        add(mainPanel);
        setVisible(true);
    }

    // Left Panel: Contains logo, welcome message, and description
    private void leftPanel() {
        leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension((int) (0.4f * WIDTH), HEIGHT)); // 40% of the width
        leftPanel.setBackground(KU_LIGHT_GRAY); // Set background color
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS)); // Vertical layout
        leftPanel.setBorder(new EmptyBorder(40, 30, 40, 30)); // Add padding

        // KU Logo
        uniLogo = new JLabel(new ImageIcon("logo//cropped-logo.png")); // Load the logo image
        uniLogo.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the logo
        uniLogo.setBorder(new EmptyBorder(15, 0, 15, 0)); // Add padding around the logo

        // Welcome Message
        welcomeLabel = new JLabel("Welcome to");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26)); // Set font and size
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the label
        welcomeLabel.setForeground(KU_WHITE); // Set text color
        welcomeLabel.setBorder(new EmptyBorder(15, 0, 5, 0)); // Add padding

        // App Name

        appNameLabel = new JLabel("Khalifa University Budget");
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 27)); // Set font and size
        appNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the label
        appNameLabel.setForeground(KU_BLUE); // Set text color
        appNameLabel.setBorder(new EmptyBorder(5, 0, 20, 0)); // Add padding

        // Description Text Area
        JTextArea descriptionArea = new JTextArea("        Your Financial Future Starts Here.");
        descriptionArea.setEditable(false); // Make the text area non-editable
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 17)); // Set font and size
        descriptionArea.setForeground(KU_WHITE); // Set text color
        descriptionArea.setBackground(KU_LIGHT_GRAY); // Set background color
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the text area
        descriptionArea.setBorder(new EmptyBorder(15, 0, 15, 0)); // Add padding
        descriptionArea.setLineWrap(true); // Enable line wrapping
        descriptionArea.setWrapStyleWord(true); // Wrap at word boundaries

        // Add description to a scroll pane
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(KU_LIGHT_GRAY, 1)); // Add border

        // Add components to the left panel
        leftPanel.add(uniLogo); // Add logo
        leftPanel.add(welcomeLabel); // Add welcome message
        leftPanel.add(appNameLabel); // Add app name
        leftPanel.add(scrollPane); // Add description
    }

    // Right Panel: Contains sign-in form and footer
    private void rightPanel() {
        rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension((int) (0.6f * WIDTH), HEIGHT)); // 60% of the width
        rightPanel.setLayout(new BorderLayout()); // Border layout for the right panel
        rightPanel.setBackground(KU_WHITE); // Set background color

        // Main content panel with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Vertical layout
        contentPanel.setBackground(KU_WHITE); // Set background color
        contentPanel.setBorder(new EmptyBorder(60, 80, 60, 80)); // Add padding

        // Sign In Label
        signLabel = new JLabel("Sign In to Continue");
        signLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the label
        signLabel.setFont(new Font("Arial", Font.BOLD, 22)); // Set font and size
        signLabel.setForeground(KU_GRAY); // Set text color
        signLabel.setBorder(new EmptyBorder(10, 0, 30, 0)); // Add padding

        // Form Panel: Contains email, password, and sign-in button
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS)); // Vertical layout
        formPanel.setBackground(KU_WHITE); // Set background color
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the form panel

        // Email Panel
        JPanel emailPanel = new JPanel(new BorderLayout()); // Border layout for email panel
        emailPanel.setBackground(KU_WHITE); // Set background color
        emailPanel.setMaximumSize(new Dimension(400, 50)); // Set maximum size

        emailLabel = new JLabel("Email: ");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Set font and size
        emailLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Add padding

        emailInput = new JTextField();
        emailInput.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font and size
        emailInput.setForeground(Color.GRAY); // Set text color
        emailInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        addPlaceholderBehavior(emailInput); // Add placeholder behavior

        emailPanel.add(emailLabel, BorderLayout.WEST); // Add label to the left
        emailPanel.add(emailInput, BorderLayout.CENTER); // Add text field to the center
        emailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, KU_GRAY), // Add border
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Password Panel
        JPanel passwordPanel = new JPanel(new BorderLayout()); // Border layout for password panel
        passwordPanel.setBackground(KU_WHITE); // Set background color
        passwordPanel.setMaximumSize(new Dimension(400, 50)); // Set maximum size

        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 20)); // Set font and size
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Add padding

        passwordInput = new JPasswordField();
        passwordInput.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font and size
        passwordInput.setForeground(Color.GRAY); // Set text color
        passwordInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        addPlaceholderBehavior(passwordInput); // Add placeholder behavior
        passwordInput.addActionListener(this);


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



        //error panel
        error = new JPanel();
        error.setLayout(new BoxLayout(error,BoxLayout.Y_AXIS));
        error.setBackground(KU_WHITE);
        error.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the panel
        error.setVisible(false);

        JLabel errorMessage = new JLabel("Please enter correct credentials. Password is case-sensitive.");
        errorMessage.setForeground(KU_RED);
        errorMessage.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font and size
        error.add(errorMessage);










        // Sign In Button
        signInButton = new JButton("Sign In");
        signInButton.setFont(new Font("Arial", Font.BOLD, 25)); // Set font and size
        signInButton.setForeground(KU_WHITE); // Set text color
        signInButton.setBackground(KU_BLUE); // Set background color
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
        signInButton.setMaximumSize(new Dimension(400, 50)); // Set maximum size
        signInButton.addActionListener(this);



        // Sign Up Panel
        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(new BoxLayout(signUpPanel, BoxLayout.X_AXIS)); // Horizontal layout
        signUpPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the panel
        signUpPanel.setBackground(KU_WHITE); // Set background color
        signUpPanel.setMaximumSize(new Dimension(400, 50)); // Set maximum size

        signUpLabel = new JLabel("Want to create a Account?");
        signUpLabel.setForeground(KU_BLUE); // Set text color
        signUpLabel.setBackground(KU_WHITE); // Set background color
        signUpLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align label to the left

        signUpButton = new JButton("Sign Up");
        signUpButton.setForeground(KU_WHITE); // Set text color
        signUpButton.setBackground(KU_BLUE); // Set background color
        signUpButton.addActionListener(this);
        signUpPanel.add(signUpLabel); // Add label
        signUpPanel.add(Box.createHorizontalGlue()); // Add glue to push the button to the right
        signUpPanel.add(signUpButton); // Add button



        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS)); // Vertical layout
        footerPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the panel
        footerPanel.setBackground(KU_WHITE); // Set background color

        JLabel helpLabel = new JLabel("Need help? Contact IT Support Center");
        helpLabel.setBackground(KU_WHITE); // Set background color
        helpLabel.setForeground(KU_LIGHT_GRAY); // Set text color

        JLabel contactLabel = new JLabel("itsupport@ku.ac.ae | +971 2 312 4444");
        contactLabel.setBackground(KU_WHITE); // Set background color
        contactLabel.setForeground(KU_LIGHT_GRAY); // Set text color

        footerPanel.add(helpLabel); // Add help label
        footerPanel.add(contactLabel); // Add contact label
        footerPanel.setBorder(new EmptyBorder(50, 0, 0, 0)); // Add padding

        // Empty Panels for Spacing
        JPanel emptySmallBetween = new JPanel();
        emptySmallBetween.setBackground(KU_WHITE); // Set background color
        emptySmallBetween.setMaximumSize(new Dimension(100, 20)); // Set maximum size

        JPanel emptyBetweenPasswordAndButton = new JPanel();
        emptyBetweenPasswordAndButton.setBackground(KU_WHITE); // Set background color
        emptyBetweenPasswordAndButton.setMaximumSize(new Dimension(100, 40)); // Set maximum size

        JPanel empty1 = new JPanel();
        empty1.setBackground(KU_WHITE); // Set background color
        empty1.setMaximumSize(new Dimension(100, 50)); // Set maximum size

        // Add components to the form panel
        formPanel.add(emptySmallBetween); // Add spacing
        formPanel.add(emailPanel); // Add email panel
        formPanel.add(emptySmallBetween); // Add spacing
        formPanel.add(passwordPanel); // Add password panel
        formPanel.add(emptyBetweenPasswordAndButton); // Add spacing
        formPanel.add(signInButton); // Add sign-in button
        formPanel.add(signUpPanel); // Add sign-up panel
        formPanel.add(empty1); // Add spacing

        // Add components to the content panel
        contentPanel.add(signLabel); // Add sign-in label
        contentPanel.add(formPanel); // Add form panel
        contentPanel.add(error);
        contentPanel.add(footerPanel,BorderLayout.SOUTH); // Add footer panel

        // Add content panel to the right panel
        rightPanel.add(contentPanel);
    }

    // Placeholder behavior for text fields
    private void addPlaceholderBehavior(JTextField textField) {
        textField.setForeground(Color.GRAY); // Set placeholder text color
    }

    // Main method
    public static void main(String[] args) {
        new SignIn(); // Create and display the frame
    }

    // ActionListener implementation
    ;    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == signInButton || e.getSource() == passwordInput) {
            String email = emailInput.getText();
            String enteredPass = new String(passwordInput.getPassword());
            try (Connection conn = NeonDBConnection.getConnection()) {
                String query = "SELECT * FROM users WHERE email = ? AND password = ? AND type = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, email);
                ps.setString(2, enteredPass);
                ps.setString(3, "user");
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + rs.getString("name"));
                    UUID id = UUID.fromString(rs.getString("id"));
                    new App(new User(id));
                    this.dispose();

                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials or user not found.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error connecting to database.");
            }
        }
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



        if(e.getSource() == signInButton){
            String email = emailInput.getText();
            String password = passwordInput.getText();

            if (email.equals("admin") && password.equals("admin2023")) {
                error.setVisible(false);
                JOptionPane.showMessageDialog(this, "Welcome Admin!");
                new AdminDashboard(); // Redirect to Admin Page
                this.dispose(); // Close SignIn page
                return;
            } else{
                error.setVisible(true);
            }
        }


        if(e.getSource()==signUpButton){
            SignUp SignUpPageObject = new SignUp();
            this.dispose();

        }
    }
}
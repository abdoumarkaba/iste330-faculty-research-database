// Authors: Haris Jukovic, Gustavo Mejia, Joann Mathews, Abderrahmane Nait Brahim
// Date: 2026-04-13 | ISTE 330

import javax.swing.*;
import java.awt.*;

/**
 * Secure login frame with password authentication.
 * Implements secure credential verification using PBKDF2 password hashing.
 */
public class LoginFrame extends JFrame {
    private JComboBox<String> userTypeCombo;
    private JTextField idField;
    private JPasswordField passwordField;
    private FacultyDAO facultyDAO;
    private StudentDAO studentDAO;

    public LoginFrame() {
        facultyDAO = new FacultyDAO();
        studentDAO = new StudentDAO();
        
        setTitle("Faculty Research Database - Secure Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 220);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));
        setResizable(false);

        add(new JLabel("  User Type:"));
        userTypeCombo = new JComboBox<>(new String[]{"Faculty", "Student", "Public"});
        add(userTypeCombo);

        add(new JLabel("  ID (Faculty/Student):"));
        idField = new JTextField();
        add(idField);
        
        add(new JLabel("  Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel(""));
        JButton loginButton = new JButton("Login");
        add(loginButton);
        
        add(new JLabel(""));
        JButton setupPasswordButton = new JButton("Set Password");
        add(setupPasswordButton);

        // Enable/disable ID and password fields based on user type
        userTypeCombo.addActionListener(e -> {
            boolean isPublic = "Public".equals(userTypeCombo.getSelectedItem());
            idField.setEnabled(!isPublic);
            passwordField.setEnabled(!isPublic);
        });

        loginButton.addActionListener(e -> handleLogin());
        
        setupPasswordButton.addActionListener(e -> handlePasswordSetup());
    }
    
    /**
     * Handles the login authentication process.
     */
    private void handleLogin() {
        String userType = (String) userTypeCombo.getSelectedItem();
        
        // Public access doesn't require authentication
        if ("Public".equals(userType)) {
            new PublicPanel().setVisible(true);
            dispose();
            return;
        }
        
        // Validate ID input
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an ID.");
            return;
        }
        
        // Validate password input
        char[] passwordChars = passwordField.getPassword();
        if (passwordChars.length == 0) {
            JOptionPane.showMessageDialog(this, "Please enter a password.");
            return;
        }
        String password = new String(passwordChars);
        
        // Clear password from memory
        java.util.Arrays.fill(passwordChars, '0');
        
        try {
            int id = Integer.parseInt(idText);
            
            // Validate ID range
            if ("Faculty".equals(userType)) {
                ValidationUtil.validateFacultyId(id);
            } else {
                ValidationUtil.validateStudentId(id);
            }
            
            // Authenticate based on user type
            if ("Faculty".equals(userType)) {
                authenticateFaculty(id, password);
            } else {
                authenticateStudent(id, password);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be a valid positive number.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    /**
     * Authenticates a faculty member.
     */
    private void authenticateFaculty(int facultyId, String password) {
        // Check if faculty exists
        if (facultyDAO.getFacultyById(facultyId) == null) {
            JOptionPane.showMessageDialog(this, "Invalid Faculty ID.");
            return;
        }
        
        // Check if account is activated
        if (!facultyDAO.isAccountActivated(facultyId)) {
            JOptionPane.showMessageDialog(this, 
                "Account not activated. Please set up your password first.");
            return;
        }
        
        // Authenticate with password
        if (facultyDAO.authenticateFaculty(facultyId, password)) {
            new FacultyPanel(facultyId).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid password.");
        }
    }
    
    /**
     * Authenticates a student.
     */
    private void authenticateStudent(int studentId, String password) {
        // Check if student exists
        if (!studentDAO.isValidStudentId(studentId)) {
            JOptionPane.showMessageDialog(this, "Invalid Student ID.");
            return;
        }
        
        // Check if account is activated
        if (!studentDAO.isAccountActivated(studentId)) {
            JOptionPane.showMessageDialog(this, 
                "Account not activated. Please set up your password first.");
            return;
        }
        
        // Authenticate with password
        if (studentDAO.authenticateStudent(studentId, password)) {
            new StudentPanel(studentId).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid password.");
        }
    }
    
    /**
     * Handles password setup for first-time users.
     */
    private void handlePasswordSetup() {
        String userType = (String) userTypeCombo.getSelectedItem();
        
        if ("Public".equals(userType)) {
            JOptionPane.showMessageDialog(this, 
                "Public users don't require passwords.");
            return;
        }
        
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an ID.");
            return;
        }
        
        try {
            int id = Integer.parseInt(idText);
            
            if ("Faculty".equals(userType)) {
                setupFacultyPassword(id);
            } else {
                setupStudentPassword(id);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be a valid number.");
        }
    }
    
    /**
     * Sets up password for a faculty member.
     */
    private void setupFacultyPassword(int facultyId) {
        // Validate faculty exists
        if (facultyDAO.getFacultyById(facultyId) == null) {
            JOptionPane.showMessageDialog(this, "Invalid Faculty ID.");
            return;
        }
        
        // Prompt for new password
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        
        Object[] message = {
            "New Password (min 8 chars, must contain uppercase, lowercase, digit, special):",
            newPasswordField,
            "Confirm Password:",
            confirmPasswordField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Set Password", 
                                                  JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            // Check passwords match
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.");
                return;
            }
            
            // Validate password strength
            String strengthCheck = PasswordUtil.checkPasswordStrength(newPassword);
            if (strengthCheck != null) {
                JOptionPane.showMessageDialog(this, 
                    "Password not strong enough: " + strengthCheck);
                return;
            }
            
            // Hash and store password
            String passwordHash = PasswordUtil.hashPassword(newPassword);
            if (facultyDAO.setPasswordHash(facultyId, passwordHash)) {
                JOptionPane.showMessageDialog(this, 
                    "Password set successfully! You can now login.");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to set password. Please try again.");
            }
        }
    }
    
    /**
     * Sets up password for a student.
     */
    private void setupStudentPassword(int studentId) {
        // Validate student exists
        if (!studentDAO.isValidStudentId(studentId)) {
            JOptionPane.showMessageDialog(this, "Invalid Student ID.");
            return;
        }
        
        // Prompt for new password
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        
        Object[] message = {
            "New Password (min 8 chars, must contain uppercase, lowercase, digit, special):",
            newPasswordField,
            "Confirm Password:",
            confirmPasswordField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Set Password", 
                                                  JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            // Check passwords match
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.");
                return;
            }
            
            // Validate password strength
            String strengthCheck = PasswordUtil.checkPasswordStrength(newPassword);
            if (strengthCheck != null) {
                JOptionPane.showMessageDialog(this, 
                    "Password not strong enough: " + strengthCheck);
                return;
            }
            
            // Hash and store password
            String passwordHash = PasswordUtil.hashPassword(newPassword);
            if (studentDAO.setPasswordHash(studentId, passwordHash)) {
                JOptionPane.showMessageDialog(this, 
                    "Password set successfully! You can now login.");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to set password. Please try again.");
            }
        }
    }
}

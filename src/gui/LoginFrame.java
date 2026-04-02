// Author: Developer | Date: 2026-03-30 | ISTE 330

package gui;

import dao.FacultyDAO;
import dao.StudentDAO;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JComboBox<String> userTypeCombo;
    private JTextField idField;

    public LoginFrame() {
        setTitle("Faculty Research Database - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 180);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));
        setResizable(false);

        add(new JLabel("  User Type:"));
        userTypeCombo = new JComboBox<>(new String[]{"Faculty", "Student", "Public"});
        add(userTypeCombo);

        add(new JLabel("  ID (Faculty/Student):"));
        idField = new JTextField();
        add(idField);

        add(new JLabel(""));
        JButton loginButton = new JButton("Login");
        add(loginButton);
        add(new JLabel(""));

        userTypeCombo.addActionListener(e ->
            idField.setEnabled(!"Public".equals(userTypeCombo.getSelectedItem())));

        loginButton.addActionListener(e -> {
            String userType = (String) userTypeCombo.getSelectedItem();
            if ("Public".equals(userType)) {
                new PublicPanel().setVisible(true);
                dispose();
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
                    if (new FacultyDAO().getFacultyById(id) != null) {
                        new FacultyPanel(id).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid Faculty ID.");
                    }
                } else {
                    if (new StudentDAO().isValidStudentId(id)) {
                        new StudentPanel(id).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid Student ID.");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID must be a number.");
            }
        });
    }
}

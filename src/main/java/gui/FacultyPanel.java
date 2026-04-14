// Author: Developer | Date: 2026-03-30 | ISTE 330

package gui;

import dao.FacultyDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FacultyPanel extends JFrame {
    private final int facultyId;
    private final FacultyDAO dao = new FacultyDAO();
    private JTextField abstractIdField;
    private JTextArea abstractTextArea;
    private JComboBox<String> typeCombo;
    private JTextField searchKeywordField;
    private DefaultTableModel tableModel;

    public FacultyPanel(int facultyId) {
        this.facultyId = facultyId;
        setTitle("Faculty Panel - ID: " + facultyId);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Abstract operations panel (NORTH)
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Abstract Management"));

        formPanel.add(new JLabel("Abstract ID (Update/Delete):"));
        abstractIdField = new JTextField();
        formPanel.add(abstractIdField);

        formPanel.add(new JLabel("Abstract Type:"));
        typeCombo = new JComboBox<>(new String[]{"book", "speaking"});
        formPanel.add(typeCombo);

        formPanel.add(new JLabel("Abstract Text:"));
        abstractTextArea = new JTextArea(2, 20);
        formPanel.add(new JScrollPane(abstractTextArea));

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton insertBtn = new JButton("Insert");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        btnPanel.add(insertBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn);
        formPanel.add(btnPanel);
        formPanel.add(new JLabel(""));

        add(formPanel, BorderLayout.NORTH);

        // Search panel (CENTER)
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Students"));
        searchPanel.add(new JLabel("Keyword:"));
        searchKeywordField = new JTextField(20);
        searchPanel.add(searchKeywordField);
        JButton searchBtn = new JButton("Search Students");
        searchPanel.add(searchBtn);
        add(searchPanel, BorderLayout.CENTER);

        // Results table (SOUTH)
        String[] cols = {"Student Name", "Email"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        // Show faculty keywords on open
        List<String> kws = dao.getFacultyKeywords(facultyId);
        JOptionPane.showMessageDialog(this, "Your keywords: " + String.join(", ", kws));

        // Listeners
        insertBtn.addActionListener(e -> {
            String text = abstractTextArea.getText().trim();
            if (text.isEmpty()) { JOptionPane.showMessageDialog(this, "Abstract text required."); return; }
            if (dao.insertAbstract(facultyId, (String) typeCombo.getSelectedItem(), text)) {
                JOptionPane.showMessageDialog(this, "Abstract inserted.");
                abstractTextArea.setText("");
            } else JOptionPane.showMessageDialog(this, "Insert failed.", "Error", JOptionPane.ERROR_MESSAGE);
        });

        updateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(abstractIdField.getText().trim());
                String text = abstractTextArea.getText().trim();
                if (text.isEmpty()) { JOptionPane.showMessageDialog(this, "Abstract text required."); return; }
                if (dao.updateAbstract(id, text)) JOptionPane.showMessageDialog(this, "Abstract updated.");
                else JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Abstract ID must be a number."); }
        });

        deleteBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(abstractIdField.getText().trim());
                int confirm = JOptionPane.showConfirmDialog(this, "Delete abstract ID " + id + "?");
                if (confirm == JOptionPane.YES_OPTION) {
                    if (dao.deleteAbstract(id)) JOptionPane.showMessageDialog(this, "Abstract deleted.");
                    else JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Abstract ID must be a number."); }
        });

        searchBtn.addActionListener(e -> {
            String kw = searchKeywordField.getText().trim();
            if (kw.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter a keyword."); return; }
            List<String> results = dao.searchStudentsByKeyword(kw);
            tableModel.setRowCount(0);
            for (String r : results) {
                String[] parts = r.split(" - ");
                tableModel.addRow(new Object[]{parts[0], parts.length > 1 ? parts[1] : ""});
            }
            if (results.isEmpty()) JOptionPane.showMessageDialog(this, "No students found.");
        });
    }
}

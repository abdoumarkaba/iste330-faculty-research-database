// Author: Developer | Date: 2026-03-30 | ISTE 330

package gui;

import dao.StudentDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JFrame {
    private final StudentDAO dao = new StudentDAO();
    private JTextField searchField;
    private DefaultTableModel tableModel;

    public StudentPanel(int studentId) {
        setTitle("Student Panel - ID: " + studentId);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Faculty"));
        searchPanel.add(new JLabel("Keyword:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("Search Faculty");
        JButton viewAllBtn = new JButton("View All Faculty");
        searchPanel.add(searchBtn);
        searchPanel.add(viewAllBtn);
        add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"Name", "Building / Office", "Email"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            if (kw.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter a keyword."); return; }
            populateTable(dao.searchFacultyByKeyword(kw));
        });

        viewAllBtn.addActionListener(e -> populateTable(dao.getAllFaculty()));
    }

    private void populateTable(List<String> results) {
        tableModel.setRowCount(0);
        for (String r : results) {
            String[] parts = r.split(" \\| ");
            tableModel.addRow(new Object[]{parts[0], parts.length > 1 ? parts[1] : "", parts.length > 2 ? parts[2] : ""});
        }
        if (results.isEmpty()) JOptionPane.showMessageDialog(this, "No results found.");
    }
}

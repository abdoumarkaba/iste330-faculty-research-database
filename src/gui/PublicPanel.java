// Author: Developer | Date: 2026-03-30 | ISTE 330

package gui;

import dao.PublicUserDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PublicPanel extends JFrame {
    private final PublicUserDAO dao = new PublicUserDAO();
    private JTextField keywordField;
    private DefaultTableModel tableModel;

    public PublicPanel() {
        setTitle("Public Search Panel");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        searchPanel.add(new JLabel("Keyword:"));
        keywordField = new JTextField(20);
        searchPanel.add(keywordField);
        JButton searchBtn = new JButton("Search");
        JButton viewAllBtn = new JButton("View All Entries");
        searchPanel.add(searchBtn);
        searchPanel.add(viewAllBtn);
        add(searchPanel, BorderLayout.NORTH);

        String[] cols = {"Type", "Name", "Email"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String kw = keywordField.getText().trim();
            if (kw.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter a keyword."); return; }
            populateTable(dao.searchByKeyword(kw));
        });

        viewAllBtn.addActionListener(e -> populateTable(dao.getAllEntries()));
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

// Author: Developer | Date: 2026-03-30 | ISTE 330

import javax.swing.SwingUtilities;
import gui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}

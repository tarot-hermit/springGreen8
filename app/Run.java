package app;

import javax.swing.SwingUtilities;

import view.LoginFrame;

public class Run {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}

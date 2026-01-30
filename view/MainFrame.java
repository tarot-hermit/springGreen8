package view;

import util.Session;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    public MainFrame() {
        if (!Session.isLoggedIn()) {
            JOptionPane.showMessageDialog(null, "세션이 없습니다. 다시 로그인하세요.");
            new LoginFrame();
            return;
        }

        setTitle("영화 예매 시스템 (회차/좌석통로/관리자등록)");
        setSize(1020, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        JLabel top = new JLabel("  " + Session.getUser().getName() + "님 환영합니다!");
        top.setFont(top.getFont().deriveFont(16f));

        JButton adminBtn = new JButton("관리자");
        adminBtn.addActionListener(e -> new AdminLoginFrame());

        topBar.add(top, BorderLayout.WEST);
        topBar.add(adminBtn, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        MyReservationPanel myPanel = new MyReservationPanel();
        BookingPanel bookingPanel = new BookingPanel(myPanel);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("예매", bookingPanel);
        tabs.addTab("내 예매", myPanel);

        add(tabs, BorderLayout.CENTER);

        setVisible(true);
    }
}

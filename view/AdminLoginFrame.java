package view;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class AdminLoginFrame extends JFrame {

    public AdminLoginFrame() {
        setTitle("관리자 로그인");
        setSize(360, 220);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfId = new JTextField(15);
        JPasswordField pfPw = new JPasswordField(15);
        JButton btn = new JButton("로그인");

        c.gridx = 0; c.gridy = 0;
        p.add(new JLabel("관리자 ID"), c);
        c.gridx = 1;
        p.add(tfId, c);

        c.gridx = 0; c.gridy = 1;
        p.add(new JLabel("관리자 PW"), c);
        c.gridx = 1;
        p.add(pfPw, c);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        p.add(btn, c);

        getRootPane().setDefaultButton(btn);

        btn.addActionListener(e -> {
            String id = tfId.getText().trim();
            String pw = new String(pfPw.getPassword());

            if ("admin".equals(id) && "1234".equals(pw)) {
                JOptionPane.showMessageDialog(this, "관리자 로그인 성공");
                dispose();
                new AdminScheduleFrame();
            } else {
                JOptionPane.showMessageDialog(this, "관리자 로그인 실패");
            }
        });

        add(p);
        setVisible(true);
    }
}

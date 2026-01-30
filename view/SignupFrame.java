package view;

import dao.BaseDAO;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class SignupFrame extends JFrame {

	public SignupFrame() {
		setTitle("회원가입");
		setSize(380, 260);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 6, 6, 6);
		c.fill = GridBagConstraints.HORIZONTAL;

		JTextField tfId = new JTextField(15);
		JPasswordField pfPw = new JPasswordField(15);
		JPasswordField pfPw2 = new JPasswordField(15);
		JTextField tfName = new JTextField(15);

		JButton btnSignup = new JButton("가입하기");
		JButton btnClose = new JButton("닫기");

		int y = 0;

		c.gridx = 0; c.gridy = y;
		p.add(new JLabel("아이디"), c);
		c.gridx = 1;
		p.add(tfId, c);

		y++;
		c.gridx = 0; c.gridy = y;
		p.add(new JLabel("비밀번호"), c);
		c.gridx = 1;
		p.add(pfPw, c);

		y++;
		c.gridx = 0; c.gridy = y;
		p.add(new JLabel("비밀번호 확인"), c);
		c.gridx = 1;
		p.add(pfPw2, c);

		y++;
		c.gridx = 0; c.gridy = y;
		p.add(new JLabel("이름"), c);
		c.gridx = 1;
		p.add(tfName, c);

		y++;
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottom.add(btnSignup);
		bottom.add(btnClose);

		c.gridx = 0; c.gridy = y; c.gridwidth = 2;
		p.add(bottom, c);

		btnClose.addActionListener(e -> dispose());

		btnSignup.addActionListener(e -> {
			String id = tfId.getText().trim();
			String pw = new String(pfPw.getPassword());
			String pw2 = new String(pfPw2.getPassword());
			String name = tfName.getText().trim();

			if (id.isEmpty() || pw.isEmpty() || pw2.isEmpty() || name.isEmpty()) {
				JOptionPane.showMessageDialog(this, "모든 항목을 입력하세요.");
				return;
			}
			if (!pw.equals(pw2)) {
				JOptionPane.showMessageDialog(this, "비밀번호 확인이 일치하지 않습니다.");
				return;
			}

			boolean ok;
			try {
				ok = new BaseDAO().register(id, pw, name);
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "회원가입 중 오류(콘솔 확인)");
				return;
			}

			if (!ok) {
				JOptionPane.showMessageDialog(this, "회원가입 실패(아이디 중복 또는 입력값 오류)");
				return;
			}

			JOptionPane.showMessageDialog(this, "회원가입 완료! 로그인 해주세요.");
			dispose();
		});

		getRootPane().setDefaultButton(btnSignup);

		add(p);
		setVisible(true);
	}
}

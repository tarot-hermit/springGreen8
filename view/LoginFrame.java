package view;

import dao.BaseDAO;
import model.UserVO;
import util.Session;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class LoginFrame extends JFrame {

	private final BaseDAO dao = new BaseDAO();

	public LoginFrame() {
		setTitle("로그인");
		setSize(360, 220);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);


		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 6, 6, 6);
		c.fill = GridBagConstraints.HORIZONTAL;

		JTextField tfId = new JTextField(15);
		JPasswordField pfPw = new JPasswordField(15);
		JButton btnLogin = new JButton("로그인");
		JButton btnSignup = new JButton("회원가입");

		c.gridx = 0; c.gridy = 0;
		p.add(new JLabel("아이디"), c);
		c.gridx = 1;
		p.add(tfId, c);

		c.gridx = 0; c.gridy = 1;
		p.add(new JLabel("비밀번호"), c);
		c.gridx = 1;
		p.add(pfPw, c);

		c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
		p.add(btnLogin, c);

		c.gridx = 0; c.gridy = 3; c.gridwidth = 2;   // ✅ 추가 줄
		p.add(btnSignup, c);

		getRootPane().setDefaultButton(btnLogin);

		btnSignup.addActionListener(e -> new SignupFrame());

		btnLogin.addActionListener(e -> {
			String id = tfId.getText().trim();
			String pw = new String(pfPw.getPassword());

			if (id.isEmpty() || pw.isEmpty()) {
				JOptionPane.showMessageDialog(this, "아이디/비밀번호를 입력하세요.");
				return;
			}

			try {
				UserVO user =  dao.login(id, pw);
				if (user == null) {
					JOptionPane.showMessageDialog(this, "로그인 실패(아이디/비밀번호 확인)");
					return;
				}
				Session.setUser(user);
				dispose();
				new MainFrame();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "DB 오류: 콘솔 로그를 확인하세요.");
				ex.printStackTrace();
			}
		});


		add(p);
		setVisible(true);
	}

}

package view;

import dao.BaseDAO;
import model.MovieVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@SuppressWarnings("serial")
public class AdminMovieManageFrame extends JFrame {

	private final BaseDAO dao = new BaseDAO();

	private final DefaultTableModel model = new DefaultTableModel(
			new Object[]{"ID", "제목", "포스터경로", "활성"}, 0
			) {
		@Override public boolean isCellEditable(int row, int col) { return false; }
	};

	private final JTable table = new JTable(model);

	private final JTextField tfTitle = new JTextField(20);
	private final JTextField tfPoster = new JTextField(20);
	private final JCheckBox chkActive = new JCheckBox("활성", true);

	private final JButton btnAdd = new JButton("추가");
	private final JButton btnUpdate = new JButton("수정");
	private final JButton btnDeactivate = new JButton("비활성화");
	private final JButton btnDelete = new JButton("삭제");
	private final JButton btnRefresh = new JButton("새로고침");
	private final JButton btnClose = new JButton("닫기");

	public AdminMovieManageFrame() {
		setTitle("관리자 - 영화 관리");
		setSize(860, 520);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(10, 10));

		// 테이블 영역
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(table), BorderLayout.CENTER);

		// 입력 폼
		JPanel form = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 6, 6, 6);
		c.fill = GridBagConstraints.HORIZONTAL;

		int y = 0;
		c.gridx = 0; c.gridy = y; form.add(new JLabel("제목"), c);
		c.gridx = 1; c.gridy = y; form.add(tfTitle, c);

		y++;
		c.gridx = 0; c.gridy = y; form.add(new JLabel("포스터 경로"), c);
		c.gridx = 1; c.gridy = y; form.add(tfPoster, c);

		y++;
		c.gridx = 1; c.gridy = y; form.add(chkActive, c);

		add(form, BorderLayout.NORTH);

		// 버튼 영역
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottom.add(btnAdd);
		bottom.add(btnUpdate);
		bottom.add(btnDeactivate);
		bottom.add(btnDelete);
		bottom.add(btnRefresh);
		bottom.add(btnClose);

		add(bottom, BorderLayout.SOUTH);

		// 이벤트
		btnClose.addActionListener(e -> dispose());
		btnRefresh.addActionListener(e -> reload());

		btnAdd.addActionListener(e -> addMovie());
		btnUpdate.addActionListener(e -> updateMovie());
		btnDeactivate.addActionListener(e -> deactivateMovie());
		btnDelete.addActionListener(e -> deleteMovie());

		table.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) return;
			fillFormFromSelectedRow();
		});

		reload();
		setVisible(true);
	}

	private void reload() {
		model.setRowCount(0);
		List<MovieVO> list = dao.getMoviesAdmin();
		for (MovieVO m : list) {
			model.addRow(new Object[]{
					m.getMovieId(),
					m.getTitle(),
					m.getPosterPath(),
					m.isActive() ? "Y" : "N"
			});
		}
		clearForm();
	}

	private void clearForm() {
		tfTitle.setText("");
		tfPoster.setText("");
		chkActive.setSelected(true);
		table.clearSelection();
	}

	private Integer selectedMovieId() {
		int row = table.getSelectedRow();
		if (row < 0) return null;
		return (Integer) model.getValueAt(row, 0);
	}

	private void fillFormFromSelectedRow() {
		int row = table.getSelectedRow();
		if (row < 0) return;

		tfTitle.setText(String.valueOf(model.getValueAt(row, 1)));
		tfPoster.setText(String.valueOf(model.getValueAt(row, 2)));
		chkActive.setSelected("Y".equals(String.valueOf(model.getValueAt(row, 3))));
	}

	private void addMovie() {
		String title = tfTitle.getText().trim();
		String poster = tfPoster.getText().trim();

		if (title.isEmpty()) {
			JOptionPane.showMessageDialog(this, "제목을 입력하세요.");
			return;
		}

		boolean ok = dao.insertMovie(title, poster);
		if (ok) {
			JOptionPane.showMessageDialog(this, "영화 추가 완료!");
			reload();
		} else {
			JOptionPane.showMessageDialog(this, "추가 실패(콘솔 확인)");
		}
	}

	private void updateMovie() {
		Integer movieId = selectedMovieId();
		if (movieId == null) {
			JOptionPane.showMessageDialog(this, "수정할 영화를 선택하세요.");
			return;
		}

		String title = tfTitle.getText().trim();
		String poster = tfPoster.getText().trim();
		boolean active = chkActive.isSelected();

		if (title.isEmpty()) {
			JOptionPane.showMessageDialog(this, "제목을 입력하세요.");
			return;
		}

		boolean ok = dao.updateMovie(movieId, title, poster, active);
		if (ok) {
			JOptionPane.showMessageDialog(this, "수정 완료!");
			reload();
		} else {
			JOptionPane.showMessageDialog(this, "수정 실패(콘솔 확인)");
		}
	}

	private void deactivateMovie() {
		Integer movieId = selectedMovieId();
		if (movieId == null) {
			JOptionPane.showMessageDialog(this, "비활성화할 영화를 선택하세요.");
			return;
		}

		int ans = JOptionPane.showConfirmDialog(this,
				"이 영화를 비활성화할까요?\n(예매 화면에서 숨김 처리됩니다)",
				"확인", JOptionPane.YES_NO_OPTION);

		if (ans != JOptionPane.YES_OPTION) return;

		boolean ok = dao.deactivateMovie(movieId);
		if (ok) {
			JOptionPane.showMessageDialog(this, "비활성화 완료!");
			reload();
		} else {
			JOptionPane.showMessageDialog(this, "비활성화 실패(콘솔 확인)");
		}
	}

	private void deleteMovie() {
		Integer movieId = selectedMovieId();
		if (movieId == null) {
			JOptionPane.showMessageDialog(this, "삭제할 영화를 선택하세요.");
			return;
		}

		int ans = JOptionPane.showConfirmDialog(this,
				"정말 삭제할까요?\n(스케줄/예약 FK가 있으면 실패할 수 있어요)\n보통은 '비활성화'를 권장!",
				"삭제 확인", JOptionPane.YES_NO_OPTION);

		if (ans != JOptionPane.YES_OPTION) return;

		boolean ok = dao.deleteMovie(movieId);
		if (ok) {
			JOptionPane.showMessageDialog(this, "삭제 완료!");
			reload();
		} else {
			JOptionPane.showMessageDialog(this, "삭제 실패(제약조건/FK 확인)");
		}
	}
}

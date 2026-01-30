package view;

import dao.BaseDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import dao.BaseDAO;
import model.MovieVO;
import model.ReservationVO;
import model.ScheduleVO;

@SuppressWarnings({ "serial", "unused" })
public class AdminSeatStatusFrame extends JFrame {

	private final JComboBox<MovieVO> cbMovie = new JComboBox<>();
	private final JComboBox<Date> cbDate = new JComboBox<>();
	private final JComboBox<ScheduleVO> cbSchedule = new JComboBox<>();

	private final DefaultTableModel model;
	private final JTable table;

	private final JPanel seatWrap = new JPanel(new BorderLayout());

	private final BaseDAO dao = new BaseDAO(); 
	
	public AdminSeatStatusFrame() {
		setTitle("관리자 - 예약 좌석 확인");
		setSize(980, 680);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(10, 10));

		// 상단 선택영역
		JPanel top = new JPanel(new GridLayout(2, 6, 8, 8));
		top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		top.add(new JLabel("영화"));
		top.add(cbMovie);
		top.add(new JLabel("날짜"));
		top.add(cbDate);
		top.add(new JLabel("회차"));
		top.add(cbSchedule);

		cbSchedule.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(
					JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {

				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				if (value instanceof ScheduleVO s) {
					String date = (s.getShowDate() != null)
							? s.getShowDate().toString()
									: "";
					String time = (s.getShowTime() != null)
							? s.getShowTime().toString().substring(0, 5)
									: "";

					setText(String.format(
							"%s | %s %s | %s",
							s.getMovieTitle(),
							date,
							time,
							s.getTheater()
							));
				} else {
					setText("");
				}

				return this;
			}
		});


		JButton btnRefresh = new JButton("조회");
		top.add(new JLabel());
		top.add(btnRefresh);

		add(top, BorderLayout.NORTH);

		// 좌측: 좌석 배치, 우측: 예약 목록
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setResizeWeight(0.55);

		seatWrap.setBorder(BorderFactory.createTitledBorder("좌석 배치(예약=빨강)"));
		split.setLeftComponent(seatWrap);

		String[] cols = {"예매번호", "좌석", "예매시간"};
		model = new DefaultTableModel(cols, 0) {
			@Override public boolean isCellEditable(int r, int c) { return false; }
		};
		table = new JTable(model);

		// ✅ 우측 패널(테이블 + 버튼)로 변경
		JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
		rightPanel.setBorder(BorderFactory.createTitledBorder("예약 목록"));

		rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);

		
		JButton btnAdminCancel = new JButton("예매취소(선택)");
		JPanel rightBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightBottom.add(btnAdminCancel);
		rightPanel.add(rightBottom, BorderLayout.SOUTH);

		split.setRightComponent(rightPanel);
		 
		
		btnAdminCancel.addActionListener(e -> {
			int row = table.getSelectedRow();
			if (row < 0) {
				JOptionPane.showMessageDialog(this, "취소할 예매를 선택하세요.");
				return;
			}

			int resId = (int) model.getValueAt(row, 0);

			int ok = JOptionPane.showConfirmDialog(this,
					"예매번호 " + resId + " 를 관리자 권한으로 취소할까요?",
					"확인", JOptionPane.YES_NO_OPTION);
			if (ok != JOptionPane.YES_OPTION) return;

			boolean done = dao.cancelByResId(resId);
			if (!done) {
				JOptionPane.showMessageDialog(this, "취소 실패(이미 취소되었을 수 있음)");
				return;
			}

			JOptionPane.showMessageDialog(this, "취소 완료!");
			loadStatus(); // 좌석/목록 다시 로딩
		});


		add(split, BorderLayout.CENTER);

		loadMovies();
		cbMovie.addActionListener(e -> onMovieChanged());
		cbDate.addActionListener(e -> onDateChanged());
		btnRefresh.addActionListener(e -> loadStatus());

		if (cbMovie.getItemCount() > 0) cbMovie.setSelectedIndex(0);

		setVisible(true);
	}

	private void loadMovies() {
		cbMovie.removeAllItems();
		for (MovieVO m : dao.getActiveMovies()) cbMovie.addItem(m);
	}

	private void onMovieChanged() {
		MovieVO movie = (MovieVO) cbMovie.getSelectedItem();
		if (movie == null) return;

		cbDate.removeAllItems();
		for (Date d : dao.getDatesByMovie(movie.getMovieId())) cbDate.addItem(d);

		if (cbDate.getItemCount() > 0) cbDate.setSelectedIndex(0);
		else {
			cbSchedule.removeAllItems();
			clearView();
		}
	}

	private void onDateChanged() {
		MovieVO movie = (MovieVO) cbMovie.getSelectedItem();
		Date date = (Date) cbDate.getSelectedItem();
		if (movie == null || date == null) return;

		cbSchedule.removeAllItems();
		for (ScheduleVO s : dao.getSchedules(movie.getMovieId(), date)) cbSchedule.addItem(s);

		if (cbSchedule.getItemCount() > 0) cbSchedule.setSelectedIndex(0);
		loadStatus();
	}

	private void clearView() {
		model.setRowCount(0);
		seatWrap.removeAll();
		seatWrap.revalidate();
		seatWrap.repaint();
	}

	private void loadStatus() {
		ScheduleVO schedule = (ScheduleVO) cbSchedule.getSelectedItem();
		if (schedule == null) {
			clearView();
			return;
		}

		int scheduleId = schedule.getScheduleId();

		// 예약 좌석 set
		Set<String> reserved = new HashSet<>(new BaseDAO().getReservedSeats(scheduleId));

		// 좌석 배치 표시
		seatWrap.removeAll();
		seatWrap.add(new AdminSeatGridPanel(reserved), BorderLayout.CENTER);
		seatWrap.revalidate();
		seatWrap.repaint();

		// 예약 목록 표시
		model.setRowCount(0);
		List<ReservationVO> list = dao.reservationsBySchedule(scheduleId);
		for (ReservationVO vo : list) {
			model.addRow(new Object[]{
					vo.getResId(),
					vo.getSeatNo(),
					vo.getReservedAt()
			});
		}
	}

	// 좌석 배치(관리자용: 클릭 없이 표시만)
	static class AdminSeatGridPanel extends JPanel {
		private static final int ROWS = 10; // A~J
		private static final int COLS = 12; // 1~12

		private static final int AISLE_AFTER_ROW = 5; // E 뒤
		private static final int AISLE_AFTER_COL = 6; // 6 뒤

		AdminSeatGridPanel(Set<String> reserved) {
			setLayout(new BorderLayout(8, 8));

			int gridRows = ROWS + 1;
			int gridCols = COLS + 1;

			JPanel grid = new JPanel(new GridLayout(gridRows, gridCols, 6, 6));
			grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			for (int r = 1; r <= gridRows; r++) {
				for (int c = 1; c <= gridCols; c++) {

					boolean aisleRow = (r == AISLE_AFTER_ROW + 1);
					boolean aisleCol = (c == AISLE_AFTER_COL + 1);

					if (aisleRow || aisleCol) {
						JPanel blank = new JPanel();
						blank.setOpaque(false);
						grid.add(blank);
						continue;
					}

					int seatRow = r;
					if (r > AISLE_AFTER_ROW + 1) seatRow--;

					int seatCol = c;
					if (c > AISLE_AFTER_COL + 1) seatCol--;

					char rowChar = (char) ('A' + (seatRow - 1));
					String seatNo = rowChar + String.valueOf(seatCol);

					JButton b = new JButton(seatNo);
					b.setEnabled(false);

					if (reserved.contains(seatNo)) {
						b.setBackground(Color.RED);
						b.setOpaque(true);
					}

					grid.add(b);
				}
			}

			add(new JScrollPane(grid), BorderLayout.CENTER);

			JLabel screen = new JLabel("SCREEN", SwingConstants.CENTER);
			screen.setOpaque(true);
			screen.setBackground(new Color(230, 230, 230));
			screen.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
			add(screen, BorderLayout.SOUTH);
		}
	}


}

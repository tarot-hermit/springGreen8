package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import core.Constants;
import dao.BaseDAO;
import model.MovieVO;
import model.ScheduleVO;
import util.Session;

/**
 * 좌석 패널
 * - A~J / 1~12 (120석)
 * - 통로 표시(가로/세로 1칸)
 * - 예약 좌석 빨강 + 비활성
 * - 클릭 시 SwingWorker로 예매(연타 방지)
 */
@SuppressWarnings("serial")
public class SeatPanel extends JPanel {

	private final BaseDAO dao = new BaseDAO();
	private final JLabel remainLabel = new JLabel();

	public SeatPanel(MovieVO movie, ScheduleVO schedule, MyReservationPanel myPanel) {
		setLayout(new BorderLayout(8, 8));

		String head = String.format("영화: %s / %s / %s %s / %d원",
				movie.getTitle(),
				schedule.getTheater(),
				schedule.getShowDate(),
				schedule.getShowTime(),
				schedule.getPrice()
				);

		JLabel info = new JLabel(head);
		info.setBorder(BorderFactory.createEmptyBorder(6, 6, 0, 6));

		JPanel top = new JPanel(new BorderLayout());
		top.add(info, BorderLayout.NORTH);

		remainLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		top.add(remainLabel, BorderLayout.SOUTH);

		add(top, BorderLayout.NORTH);

		int scheduleId = schedule.getScheduleId();
		Set<String> reserved = dao.getReservedSeats(scheduleId);
		updateRemain(scheduleId);

		int gridRows = Constants.SEAT_ROWS + 1;
		int gridCols = Constants.SEAT_COLS + 1;

		JPanel grid = new JPanel(new GridLayout(gridRows, gridCols, 6, 6));
		grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		for (int r = 1; r <= gridRows; r++) {
			for (int c = 1; c <= gridCols; c++) {

				boolean aisleRow = (r == Constants.AISLE_AFTER_ROW + 1);
				boolean aisleCol = (c == Constants.AISLE_AFTER_COL + 1);

				if (aisleRow || aisleCol) {
					JPanel blank = new JPanel();
					blank.setOpaque(false);
					grid.add(blank);
					continue;
				}

				int seatRow = r;
				if (r > Constants.AISLE_AFTER_ROW + 1) seatRow--;

				int seatCol = c;
				if (c > Constants.AISLE_AFTER_COL + 1) seatCol--;

				char rowChar = (char) ('A' + (seatRow - 1));
				String seatNo = rowChar + String.valueOf(seatCol);

				JButton b = new JButton(seatNo);

				if (reserved.contains(seatNo)) markReserved(b);

				b.addActionListener(e -> {

					String msg = String.format("이 좌석을 예매하시겠습니까?\n\n영화: %s\n상영관: %s\n날짜: %s\n시간: %s\n좌석: %s\n가격: %d원",
							movie.getTitle(),
							schedule.getTheater(),
							schedule.getShowDate(),
							schedule.getShowTime(),
							seatNo,
							schedule.getPrice()
							);

					int choice = JOptionPane.showConfirmDialog(SeatPanel.this, msg,"예매확인",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);

					if(choice!= JOptionPane.YES_OPTION) {
						return;
					}

					b.setEnabled(false); // 연타 방지

					new SwingWorker<Boolean, Void>() {
						@Override
						protected Boolean doInBackground() {
							return dao.reserve(Session.getUser().getUserId(), scheduleId, seatNo);
						}

						@Override
						protected void done() {
							boolean ok;
							try {
								ok = get();
							} catch (Exception ex) {
								JOptionPane.showMessageDialog(SeatPanel.this, "예매 중 오류가 발생했습니다.");
								b.setEnabled(true);
								return;
							}

							if (!ok) {
								JOptionPane.showMessageDialog(SeatPanel.this, "이미 예매된 좌석입니다.");
								markReserved(b);
								updateRemain(scheduleId);
								return;
							}

							markReserved(b);
							JOptionPane.showMessageDialog(SeatPanel.this, "예매 완료! (" + seatNo + ")");
							myPanel.refresh();
							updateRemain(scheduleId);
						}
					}.execute();
				});

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

	private void markReserved(JButton b) {
		b.setBackground(Color.RED);
		b.setOpaque(true);
		b.setEnabled(false);
	}

	private void updateRemain(int scheduleId) {
		int reservedCount = dao.countReserved(scheduleId);
		int remain = Math.max(0, Constants.TOTAL_SEATS - reservedCount);
		remainLabel.setText("잔여좌석: " + remain + " / " + Constants.TOTAL_SEATS);
	}
}

package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import dao.BaseDAO;
import model.MovieVO;
import model.ScheduleVO;

@SuppressWarnings("serial")
public class AdminScheduleFrame extends JFrame {

	private final JComboBox<MovieVO> cbMovie = new JComboBox<>();
	private final JTextField tfTheater = new JTextField("1관", 10);
	private final JSpinner spDate;
	private final JSpinner spTime;
	private final JTextField tfPrice = new JTextField("12000", 10);

	private final DefaultTableModel model = new DefaultTableModel(new String[] {"ID","영화","상영관","날짜","시간","가격"},0) {
		@Override public boolean isCellEditable(int r , int c) {return false;}
	};
	private final JTable table = new JTable(model);
	private final JButton btnMovieManage = new JButton("영화 관리");

	private final JButton btnInsert = new JButton("회차 등록");
	private final JButton btnSeatStatus = new JButton("예약좌석 확인");
	private final JButton btnClose = new JButton("닫기");
	private final JButton	btnRefresh = new JButton("새로고침");
	private final JButton btnDelete = new JButton("회차삭제");

	private final BaseDAO dao = new BaseDAO();

	public AdminScheduleFrame() {
		setTitle("관리자 - 회차 등록");
		setSize(600, 340);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);

		SpinnerDateModel dateModel = new SpinnerDateModel();
		spDate = new JSpinner(dateModel);
		spDate.setEditor(new JSpinner.DateEditor(spDate, "yyyy-MM-dd"));

		SpinnerDateModel timeModel = new SpinnerDateModel();
		spTime = new JSpinner(timeModel);
		spTime.setEditor(new JSpinner.DateEditor(spTime, "HH:mm"));

		JPanel form = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(8, 8, 8, 8);
		c.fill = GridBagConstraints.HORIZONTAL;

		int y = 0;

		c.gridx = 0; c.gridy = y;
		form.add(new JLabel("영화"), c);
		c.gridx = 1;
		form.add(cbMovie, c);

		y++;
		c.gridx = 0; c.gridy = y;
		form.add(new JLabel("상영관"), c);
		c.gridx = 1;
		form.add(tfTheater, c);

		y++;
		c.gridx = 0; c.gridy = y;
		form.add(new JLabel("날짜"), c);
		c.gridx = 1;
		form.add(spDate, c);

		y++;
		c.gridx = 0; c.gridy = y;
		form.add(new JLabel("시간"), c);
		c.gridx = 1;
		form.add(spTime, c);

		y++;
		c.gridx = 0; c.gridy = y;
		form.add(new JLabel("가격"), c);
		c.gridx = 1;
		form.add(tfPrice, c);

		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottom.add(btnRefresh);
		bottom.add(btnDelete);
		bottom.add(btnInsert);
		bottom.add(btnSeatStatus);
		bottom.add(btnClose);
		bottom.add(btnMovieManage);

		btnDelete.addActionListener(e-> deleteSelectedScheduleAsync());
		btnRefresh.addActionListener(e -> reFreshUIAsync());
		btnClose.addActionListener(e -> dispose());
		btnInsert.addActionListener(e -> insertScheduleAsync());
		btnSeatStatus.addActionListener(e -> new AdminSeatStatusFrame());
		btnMovieManage.addActionListener(e -> new AdminMovieManageFrame());

		JPanel center = new JPanel(new BorderLayout(8, 8));
		center.add(form, BorderLayout.NORTH);

		JScrollPane sp = new JScrollPane(table);
		sp.setBorder(BorderFactory.createTitledBorder("회차 목록"));
		center.add(sp, BorderLayout.CENTER);

		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);

		loadMovies();
		loadSchedulesAsync();
		setVisible(true);
	}


	private void loadSchedulesAsync() {
		setBusy(true);

		new SwingWorker<List<ScheduleVO>, Void>() {
			@Override protected List<ScheduleVO> doInBackground() {
				return dao.getSchedulesAdmin(); // ✅ BaseDAO에 추가 필요
			}

			@Override protected void done() {
				try {
					List<ScheduleVO> list = get();
					model.setRowCount(0);
					for (ScheduleVO s : list) {
						model.addRow(new Object[]{
								s.getScheduleId(),
								s.getTitle(),       
								s.getTheater(),
								s.getShowDate(),
								s.getShowTime(),
								s.getPrice()
						});
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(AdminScheduleFrame.this, "회차 목록 로딩 실패");
				} finally {
					setBusy(false);
				}
			}
		}.execute();
	}

	private void deleteSelectedScheduleAsync() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "삭제할 회차를 선택하세요.");
			return;
		}

		int scheduleId = (int) model.getValueAt(row, 0);

		int choice = JOptionPane.showConfirmDialog(
				this,
				"선택한 회차를 삭제하시겠습니까?\n\nID: " + scheduleId,
				"회차 삭제",
				JOptionPane.YES_NO_OPTION
				);
		if (choice != JOptionPane.YES_OPTION) return;

		setBusy(true);

		new SwingWorker<Boolean, Void>() {
			@Override protected Boolean doInBackground() {
				return dao.deleteSchedule(scheduleId); 
			}

			@Override protected void done() {
				try {
					boolean ok = get();
					if (!ok) {
						JOptionPane.showMessageDialog(AdminScheduleFrame.this,
								"삭제 실패: 예약이 있는 회차이거나 제약조건 위반입니다.");
						return;
					}
					JOptionPane.showMessageDialog(AdminScheduleFrame.this, "삭제 완료!");
					loadSchedulesAsync(); 
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(AdminScheduleFrame.this, "삭제 중 오류 발생");
				} finally {
					setBusy(false);
				}
			}
		}.execute();
	}


	private void reFreshUIAsync() {
		setBusy(true);

		new SwingWorker<List<MovieVO>, Void>() {
			@Override
			protected List<MovieVO> doInBackground() {

				return dao.getActiveMovies();
			}
			@Override
			protected void done() {
				try {
					List<MovieVO> list = get();

					cbMovie.removeAllItems();
					for(MovieVO m : list) cbMovie.addItem(m);
					if(cbMovie.getItemCount() > 0 ) cbMovie.setSelectedIndex(0);

					JOptionPane.showMessageDialog(AdminScheduleFrame.this, "새로고침 완료");
				}catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(AdminScheduleFrame.this, "새로고침 중 오류 발생");
				}finally {
					setBusy(false);
				}
			}
		}.execute();
	}


	private void loadMovies() {
		cbMovie.removeAllItems();
		List<MovieVO> list = dao.getActiveMovies();
		for (MovieVO m : list) cbMovie.addItem(m);
		if (cbMovie.getItemCount() > 0) cbMovie.setSelectedIndex(0);
	}

	private void insertScheduleAsync() {
		MovieVO movie = (MovieVO) cbMovie.getSelectedItem();
		if (movie == null) {
			JOptionPane.showMessageDialog(this, "영화를 선택하세요.");
			return;
		}

		String theater = tfTheater.getText().trim();
		if (theater.isEmpty()) {
			JOptionPane.showMessageDialog(this, "상영관을 입력하세요.");
			return;
		}

		int price;
		try {
			price = Integer.parseInt(tfPrice.getText().trim());
			if (price <= 0) throw new NumberFormatException();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "가격은 양의 정수로 입력하세요.");
			return;
		}

		java.util.Date utilDate = (java.util.Date) spDate.getValue();
		java.util.Date utilTime = (java.util.Date) spTime.getValue();

		Date showDate = new Date(utilDate.getTime());

		Calendar cal = Calendar.getInstance();
		cal.setTime(utilTime);
		int hh = cal.get(Calendar.HOUR_OF_DAY);
		int mm = cal.get(Calendar.MINUTE);
		Time showTime = Time.valueOf(String.format("%02d:%02d:00", hh, mm));

		setBusy(true);

		new SwingWorker<Boolean, Void>() {
			@Override protected Boolean doInBackground() {
				return dao.insertSchedule(movie.getMovieId(), theater, showDate, showTime, price);
			}

			@Override protected void done() {
				try {
					boolean ok = get();
					if (!ok) {
						JOptionPane.showMessageDialog(AdminScheduleFrame.this,
								"등록 실패: 중복 회차이거나 제약조건 위반입니다.");
						return;
					}
					JOptionPane.showMessageDialog(AdminScheduleFrame.this, "회차 등록 완료!");
					reFreshUIAsync();

				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(AdminScheduleFrame.this, "등록 중 오류 발생(콘솔 확인)");
				} finally {
					setBusy(false);
				}
			}
		}.execute();
	}

	private void setBusy(boolean busy) {
		btnInsert.setEnabled(!busy);
		btnClose.setEnabled(!busy);
		btnSeatStatus.setEnabled(!busy);
		btnRefresh.setEnabled(!busy);      
		btnMovieManage.setEnabled(!busy);  
		btnDelete.setEnabled(!busy);

		cbMovie.setEnabled(!busy);
		tfTheater.setEnabled(!busy);
		spDate.setEnabled(!busy);
		spTime.setEnabled(!busy);
		tfPrice.setEnabled(!busy);
	}
}

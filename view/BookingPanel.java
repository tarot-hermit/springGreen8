package view;

import dao.BaseDAO;
import model.MovieVO;
import model.ScheduleVO;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.util.List;

@SuppressWarnings("serial")
public class BookingPanel extends JPanel {

	private final JComboBox<MovieVO> cbMovie = new JComboBox<>();
	private final JComboBox<Date> cbDate = new JComboBox<>();
	private final JComboBox<ScheduleVO> cbSchedule = new JComboBox<>();

	private final JLabel posterLabel = new JLabel("포스터 없음", SwingConstants.CENTER);
	private final JPanel seatWrap = new JPanel(new BorderLayout());

	private final MyReservationPanel myPanel;

	private final BaseDAO dao = new BaseDAO();

	public BookingPanel(MyReservationPanel myPanel) {
		this.myPanel = myPanel;

		setLayout(new BorderLayout(10, 10));


		JPanel left = new JPanel(new BorderLayout(10, 10));
		left.setPreferredSize(new Dimension(380, 10));

		posterLabel.setBorder(BorderFactory.createTitledBorder("포스터"));
		left.add(posterLabel, BorderLayout.CENTER);

		JButton btnRefresh = new JButton("새로고침");
		btnRefresh.addActionListener(e -> loadMoviesAsync());

		JPanel btnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		btnBox.add(btnRefresh);

		left.add(btnBox, BorderLayout.SOUTH);


		JPanel select = new JPanel(new GridLayout(6, 1, 6, 6));
		select.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		select.add(new JLabel("영화"));
		select.add(cbMovie);
		select.add(new JLabel("날짜"));
		select.add(cbDate);
		select.add(new JLabel("회차(시간/관/가격)"));
		select.add(cbSchedule);

		cbSchedule.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof ScheduleVO s) {
					setText(s.getShowTime() + " / " + s.getTheater() + " / " + s.getPrice() + "원");
				}
				return this;
			}
		});

		left.add(select, BorderLayout.NORTH);

		seatWrap.setBorder(BorderFactory.createTitledBorder("좌석 선택"));
		add(left, BorderLayout.WEST);
		add(seatWrap, BorderLayout.CENTER);

		cbMovie.addActionListener(e -> onMovieChanged());
		cbDate.addActionListener(e -> onDateChanged());
		cbSchedule.addActionListener(e -> onScheduleChanged());

		loadMoviesAsync();
	}

	private void loadMoviesAsync() {
		setLoadingState(true);
		new SwingWorker<List<MovieVO>, Void>() {
			@Override protected List<MovieVO> doInBackground() { return dao.getActiveMovies(); }
			@Override protected void done() {
				try {
					List<MovieVO> movies = get();
					cbMovie.removeAllItems();
					for (MovieVO m : movies) cbMovie.addItem(m);

					if (cbMovie.getItemCount() > 0) cbMovie.setSelectedIndex(0);
					else clearRight();
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(BookingPanel.this, "영화 목록 로딩 실패");
					clearRight();
				} finally {
					setLoadingState(false);
				}
			}
		}.execute();
	}

	private void onMovieChanged() {
		MovieVO movie = (MovieVO) cbMovie.getSelectedItem();
		if (movie == null) return;

		ImageIcon icon = ImageUtil.loadPoster(movie.getPosterPath(), 320, 460);
		posterLabel.setText(icon == null ? "포스터 없음" : "");
		posterLabel.setIcon(icon);

		cbDate.removeAllItems();
		cbSchedule.removeAllItems();
		seatWrap.removeAll();

		setLoadingState(true);
		new SwingWorker<List<Date>, Void>() {
			@Override protected List<Date> doInBackground() { return dao.getDatesByMovie(movie.getMovieId()); }
			@Override protected void done() {
				try {
					List<Date> dates = get();
					cbDate.removeAllItems();
					for (Date d : dates) cbDate.addItem(d);

					if (cbDate.getItemCount() > 0) cbDate.setSelectedIndex(0);
					else clearRight();
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(BookingPanel.this, "날짜 로딩 실패");
					clearRight();
				} finally {
					setLoadingState(false);
				}
			}
		}.execute();
	}

	private void onDateChanged() {
		MovieVO movie = (MovieVO) cbMovie.getSelectedItem();
		Date date = (Date) cbDate.getSelectedItem();
		if (movie == null || date == null) return;

		cbSchedule.removeAllItems();
		seatWrap.removeAll();

		setLoadingState(true);
		new SwingWorker<List<ScheduleVO>, Void>() {
			@Override protected List<ScheduleVO> doInBackground() { return dao.getSchedules(movie.getMovieId(), date); }
			@Override protected void done() {
				try {
					List<ScheduleVO> schedules = get();
					cbSchedule.removeAllItems();
					for (ScheduleVO s : schedules) cbSchedule.addItem(s);

					if (cbSchedule.getItemCount() > 0) cbSchedule.setSelectedIndex(0);
					else clearRight();
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(BookingPanel.this, "회차 로딩 실패");
					clearRight();
				} finally {
					setLoadingState(false);
				}
			}
		}.execute();
	}

	private void onScheduleChanged() {
		MovieVO movie = (MovieVO) cbMovie.getSelectedItem();
		ScheduleVO schedule = (ScheduleVO) cbSchedule.getSelectedItem();
		if (movie == null || schedule == null) return;

		seatWrap.removeAll();
		seatWrap.add(new SeatPanel(movie, schedule, myPanel), BorderLayout.CENTER);
		seatWrap.revalidate();
		seatWrap.repaint();
	}

	private void clearRight() {
		cbSchedule.removeAllItems();
		seatWrap.removeAll();
		seatWrap.revalidate();
		seatWrap.repaint();
	}

	private void setLoadingState(boolean loading) {
		cbMovie.setEnabled(!loading);
		cbDate.setEnabled(!loading);
		cbSchedule.setEnabled(!loading);
	}

	static class ImageUtil {
		static ImageIcon loadPoster(String path, int w, int h) {
			try {
				if (path == null || path.isBlank()) return null;


				String p = path.trim().replace("\\", "/");
				String cp = path.startsWith("/") ? path.substring(1) : path;

				java.awt.image.BufferedImage bi = null;

				java.net.URL url = BookingPanel.class.getClassLoader().getResource(cp);
				if (url != null) {
					bi = javax.imageio.ImageIO.read(url);
				} else {

					java.io.File file = new java.io.File(p);
					if(!file.isAbsolute()) {
						file = new java.io.File(System.getProperty("user.dir"), p);
					}
					if (!file.exists()) {
						System.out.println("[포스터 로딩 실패] " + file.getAbsolutePath());
						return null;
					}
					bi = javax.imageio.ImageIO.read(file);
				}

				if(bi == null) {
					System.out.println("[포스터 로딩 실패] ImageIO.read returned null :" + path);
					return null;
				}

				Image scaled = bi.getScaledInstance(w, h, Image.SCALE_SMOOTH);
				return new ImageIcon(scaled);

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("[포스터 로딩 예외]" + path);
				return null;
			}
		}
	}
}

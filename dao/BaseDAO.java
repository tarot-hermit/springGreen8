package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exception.DataAccessException;
import model.MovieVO;
import model.ReservationVO;
import model.ScheduleVO;
import model.UserVO;
import util.DBUtil;


public class BaseDAO {

	@FunctionalInterface
	protected interface SQLFunction<T> {
		T apply(Connection con) throws SQLException;
	}

	protected <T> T withConnection(SQLFunction<T> fn) {
		try (Connection con = DBUtil.getConnection()) {
			return fn.apply(con);
		} catch (SQLException e) {
			throw new DataAccessException("DB 처리 중 오류가 발생했습니다.", e);
		}
	}




	//✅ 관리자용: 영화 목록(활성/비활성 포함)
	public List<MovieVO> getMoviesAdmin() {
		final String sql = "SELECT movie_id, title, poster_path, active FROM movie ORDER BY movie_id";
		return withConnection(con -> {
			List<MovieVO> list = new ArrayList<>();
			try (PreparedStatement ps = con.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					MovieVO vo = new MovieVO();
					vo.setMovieId(rs.getInt("movie_id"));
					vo.setTitle(rs.getString("title"));
					vo.setPosterPath(rs.getString("poster_path"));
					vo.setActive(rs.getInt("active") == 1);
					list.add(vo);
				}
			}
			return list;
		});
	}



	//✅ 사용자/예매용: 활성 영화만(기존 getMovies() 대체용으로 쓰면 깔끔)
	public List<MovieVO> getActiveMovies() {
		final String sql = """
				    SELECT movie_id, title, poster_path, active
				    FROM movie
				    WHERE active = 1
				    ORDER BY movie_id
				""";

		return withConnection(con -> {
			List<MovieVO> list = new ArrayList<>();
			try (PreparedStatement ps = con.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					MovieVO vo = new MovieVO();
					vo.setMovieId(rs.getInt("movie_id"));
					vo.setTitle(rs.getString("title"));
					vo.setPosterPath(rs.getString("poster_path"));
					vo.setActive(true);
					list.add(vo);
				} 
			}    
			return list;
		});
	}

	// 영화 추가
	public boolean insertMovie(String title, String posterPath) {
		final String sql = "INSERT INTO movie(title, poster_path, active) VALUES(?,?,1)";
		return withConnection(con -> {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1, title);
				ps.setString(2, posterPath);
				return ps.executeUpdate() == 1;
			}
		});
	}

	// 영화 수정
	public boolean updateMovie(int movieId, String title, String posterPath, boolean active) {
		final String sql = "UPDATE movie SET title=?, poster_path=?, active=? WHERE movie_id=?";
		return withConnection(con -> {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1, title);
				ps.setString(2, posterPath);
				ps.setInt(3, active ? 1 : 0);
				ps.setInt(4, movieId);
				return ps.executeUpdate() == 1;
			}
		});
	}

	// 영화 비활성화(soft delete)
	public boolean deactivateMovie(int movieId) {
		final String sql = "UPDATE movie SET active=0 WHERE movie_id=?";
		return withConnection(con -> {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, movieId);
				return ps.executeUpdate() == 1;
			}
		});
	}

	// 영화 삭제(진짜 삭제 - 선택 기능)
	// 스케줄/예약 FK 걸려있으면 실패할 수 있으니 기본은 비활성화 권장
	public boolean deleteMovie(int movieId) {
		final String sql = "DELETE FROM movie WHERE movie_id=?";
		return withConnection(con -> {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, movieId);
				return ps.executeUpdate() == 1;
			}
		});
	}



	public Set<String> getReservedSeats(int scheduleId) {
		final String sql = "select seat_no from reservation where schedule_id=?";
		return withConnection(con -> {
			Set<String> set = new HashSet<>();
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, scheduleId);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) set.add(rs.getString("seat_no"));
				}
			}
			return set;
		});
	}

	public int countReserved(int scheduleId) {
		final String sql = "select count(*) from reservation where schedule_id=?";
		return withConnection(con -> {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, scheduleId);
				try (ResultSet rs = ps.executeQuery()) {
					return rs.next() ? rs.getInt(1) : 0;
				}
			}
		});
	}

	public boolean reserve(int userId, int scheduleId, String seatNo) {
		final String sql = "insert into reservation(user_id, schedule_id, seat_no) values(?,?,?)";

		return withConnection(con -> {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, userId);
				ps.setInt(2, scheduleId);
				ps.setString(3, seatNo);
				ps.executeUpdate();
				return true;
			} catch (SQLIntegrityConstraintViolationException dup) {				
				return false;
			}
		});
	}

	public List<ReservationVO> myReservations(int userId) {
		final String sql = "SELECT r.res_id, m.title, s.show_date, s.show_time, s.theater, s.price, r.seat_no, r.reserved_at FROM reservation r JOIN schedule s ON r.schedule_id = s.schedule_id  JOIN movie m ON s.movie_id = m.movie_id WHERE r.user_id=? ORDER BY r.reserved_at DESC";

		return withConnection(con -> {
			List<ReservationVO> list = new ArrayList<>();
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, userId);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						ReservationVO vo = new ReservationVO();
						vo.setResId(rs.getInt("res_id"));
						vo.setMovieTitle(rs.getString("title"));
						vo.setShowDate(rs.getDate("show_date"));
						vo.setShowTime(rs.getTime("show_time"));
						vo.setTheater(rs.getString("theater"));
						vo.setPrice(rs.getInt("price"));
						vo.setSeatNo(rs.getString("seat_no"));
						vo.setReservedAt(rs.getTimestamp("reserved_at"));
						list.add(vo);
					}
				}
			}
			return list;
		});
	}

	public boolean cancel(int userId, int resId) {
		final String sql = "DELETE FROM reservation WHERE res_id=? AND user_id=?";
		return withConnection(con -> {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, resId);
				ps.setInt(2, userId);
				return ps.executeUpdate() == 1;
			}
		});
	}

	public boolean cancelByResId(int resId) {
		final String sql = "DELETE FROM reservation WHERE res_id=?";

		return withConnection(con -> {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, resId);
				return ps.executeUpdate() == 1; 
			}
		});
	}

	public List<ReservationVO> reservationsBySchedule(int scheduleId) {
		List<ReservationVO> list = new ArrayList<>();

		String sql = "select r.res_id,u.login_id, u.name, r.seat_no,r.reserved_at from reservation r join users u ON r.user_id = u.user_id where r.schedule_id = ? order by r.seat_no ";

		return withConnection(con -> {
			try(PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, scheduleId);

				try(ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						ReservationVO vo = new ReservationVO();
						vo.setResId(rs.getInt("res_id"));
						vo.setSeatNo(rs.getString("seat_no"));
						vo.setReservedAt(rs.getTimestamp("reserved_at"));

						list.add(vo);
					}
				}
				return list;
			}
		});
	}


	public List<Date> getDatesByMovie(int movieId) {
		final String sql = "SELECT DISTINCT show_date FROM schedule WHERE movie_id=? ORDER BY show_date";

		return withConnection(con -> {
			List<Date> list = new ArrayList<>();
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, movieId);
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) list.add(rs.getDate("show_date"));
				}
			}
			return list;
		});
	}

	public List<ScheduleVO> getSchedules(int movieId, Date showDate) {
		final String sql = """
				    SELECT s.schedule_id, s.movie_id, m.title AS title,
				       s.theater, s.show_date, s.show_time, s.price
				FROM schedule s
				JOIN movie m ON m.movie_id = s.movie_id
				WHERE s.movie_id=? AND s.show_date=?
				ORDER BY s.show_time
				""";

		return withConnection(con -> {
			List<ScheduleVO> list = new ArrayList<>();
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, movieId);
				ps.setDate(2, showDate);

				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						ScheduleVO vo = new ScheduleVO();
						vo.setScheduleId(rs.getInt("schedule_id"));
						vo.setMovieId(rs.getInt("movie_id"));
						vo.setMovieTitle(rs.getString("title"));
						vo.setTheater(rs.getString("theater"));
						vo.setShowDate(rs.getDate("show_date"));
						vo.setShowTime(rs.getTime("show_time"));
						vo.setPrice(rs.getInt("price"));
						list.add(vo);
					}
				}
			}
			return list;
		});
	}

	/**
	 * 회차 등록
	 * - schedule 테이블에 UNIQUE(movie_id, theater, show_date, show_time) 존재 시 중복 등록 false 처리 가능
	 */
	public boolean insertSchedule(int movieId, String theater, Date showDate, Time showTime, int price) {
		final String sql = """
				    INSERT INTO schedule(movie_id, theater, show_date, show_time, price)
				    VALUES(?,?,?,?,?)
				""";

		return withConnection(con -> {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, movieId);
				ps.setString(2, theater);
				ps.setDate(3, showDate);
				ps.setTime(4, showTime);
				ps.setInt(5, price);
				return ps.executeUpdate() == 1;   // ✅ 성공 여부 확실
			} catch (SQLIntegrityConstraintViolationException dup) {
				return false; // ✅ 중복이면 false
			}
		});
	}


	public UserVO login(String loginId, String password) {
		if (loginId == null || loginId.isBlank()) return null;

		final String pw = (password == null) ? "" : password;
		final String sql = "select user_id, login_id, name from users where login_id=? and password=?";

		return withConnection(con -> {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1, loginId.trim());
				ps.setString(2, pw);

				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next()) return null;

					UserVO vo = new UserVO();
					vo.setUserId(rs.getInt("user_id"));
					vo.setLoginId(rs.getString("login_id"));
					vo.setName(rs.getString("name"));
					return vo;
				}
			}
		});
	}

	// ✅ 추가: 회원가입
	public boolean register(String loginId, String password, String name) {
		if (loginId == null || loginId.isBlank()) return false;
		if (password == null || password.isBlank()) return false;
		if (name == null || name.isBlank()) return false;

		final String sql = "insert into users(login_id, password, name) values(?,?,?)";

		return withConnection(con -> {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1, loginId.trim());
				ps.setString(2, password);
				ps.setString(3, name.trim());
				ps.executeUpdate();
				return true;
			} catch (SQLIntegrityConstraintViolationException dup) {
				// login_id UNIQUE 충돌(아이디 중복)
				return false;
			}
		});
	}




	public List<ScheduleVO> getSchedulesAdmin() {

		final String sql = """
				    SELECT s.schedule_id,
				           s.movie_id,
				           m.title,
				           s.show_date,
				           s.show_time,
				           s.theater,
				           s.price
				    FROM schedule s
				    JOIN movie m ON s.movie_id = m.movie_id
				    ORDER BY s.show_date DESC, s.show_time DESC, s.schedule_id DESC
				""";

		return withConnection(con -> {
			List<ScheduleVO> list = new ArrayList<>();

			try (PreparedStatement ps = con.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					ScheduleVO vo = new ScheduleVO();
					vo.setScheduleId(rs.getInt("schedule_id"));
					vo.setMovieId(rs.getInt("movie_id"));

					// ⭐ AdminScheduleFrame JTable에서 쓰는 영화 제목
					vo.setTitle(rs.getString("title"));

					vo.setShowDate(rs.getDate("show_date"));
					vo.setShowTime(rs.getTime("show_time"));
					vo.setTheater(rs.getString("theater"));
					vo.setPrice(rs.getInt("price"));

					list.add(vo);
				}
			}
			return list;
		});
	}




	public boolean deleteSchedule(int scheduleId) {

		final String sql = "DELETE FROM schedule WHERE schedule_id=?";

		return withConnection(con -> {
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setInt(1, scheduleId);
				return ps.executeUpdate() == 1;

			} catch (SQLIntegrityConstraintViolationException e) {
				// 예약(reservation)이 있는 회차 → FK 제약으로 여기로 들어옴
				return false;
			}
		});
	}
}

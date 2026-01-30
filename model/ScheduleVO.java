package model;

import java.sql.Date;
import java.sql.Time;

/**
 * 회차(상영 스케줄) 정보
 * 표시 포맷은 View 렌더러에서 처리(모델은 데이터만)
 */
public class ScheduleVO {
    private int scheduleId;
    private int movieId;
    private String movieTitle;
		private String theater;
    private Date showDate;
    private Time showTime;
    private int price;
    private String title;

    public String getTitle() {return title;}
		public void setTitle(String title) {this.title = title;}
		
		public String getMovieTitle() {return movieTitle;}
    public void setMovieTitle(String movieTitle) {this.movieTitle = movieTitle;}

		public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getTheater() { return theater; }
    public void setTheater(String theater) { this.theater = theater; }

    public Date getShowDate() { return showDate; }
    public void setShowDate(Date showDate) { this.showDate = showDate; }

    public Time getShowTime() { return showTime; }
    public void setShowTime(Time showTime) { this.showTime = showTime; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
   
    @Override
		public String toString() {
			return "ScheduleVO [scheduleId=" + scheduleId + ", movieId=" + movieId + ", movieTitle=" + movieTitle
					+ ", theater=" + theater + ", showDate=" + showDate + ", showTime=" + showTime + ", price=" + price
					+ ", title=" + title + "]";
		}
    
}

package model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * 내 예매 목록 표시용 VO (reservation + schedule + movie join 결과)
 */
public class ReservationVO {
    private int resId;
    private String movieTitle;
    private Date showDate;
    private Time showTime;
    private String theater;
    private String seatNo;
    private int price;
    private Timestamp reservedAt;

    public int getResId() { return resId; }
    public void setResId(int resId) { this.resId = resId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public Date getShowDate() { return showDate; }
    public void setShowDate(Date showDate) { this.showDate = showDate; }

    public Time getShowTime() { return showTime; }
    public void setShowTime(Time showTime) { this.showTime = showTime; }

    public String getTheater() { return theater; }
    public void setTheater(String theater) { this.theater = theater; }

    public String getSeatNo() { return seatNo; }
    public void setSeatNo(String seatNo) { this.seatNo = seatNo; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public Timestamp getReservedAt() { return reservedAt; }
    public void setReservedAt(Timestamp reservedAt) { this.reservedAt = reservedAt; }
}

package core;

public final class Constants {
    private Constants() {}

    // 좌석 설정
    public static final int SEAT_ROWS = 10;     // A~J
    public static final int SEAT_COLS = 12;     // 1~12
    public static final int TOTAL_SEATS = SEAT_ROWS * SEAT_COLS;

    // 통로 위치(예시)
    public static final int AISLE_AFTER_ROW = 5; // E 뒤
    public static final int AISLE_AFTER_COL = 6; // 6 뒤

    // DB
    public static final String DB_URL = "jdbc:mysql://localhost:3306/springgreen8";
    public static final String DB_USER = "atom";
    public static final String DB_PASSWORD = "1234"; 
}

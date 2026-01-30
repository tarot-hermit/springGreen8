package util;

import core.Constants;
import exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBUtil {
    private static volatile boolean driverLoaded = false;

    private DBUtil() {}

    private static void loadDriverOnce() {
        if (driverLoaded) return;
        synchronized (DBUtil.class) {
            if (driverLoaded) return;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new DataAccessException("MySQL JDBC 드라이버를 찾을 수 없습니다.", e);
            }
        }
    }

    public static Connection getConnection() {
        loadDriverOnce();
        try {
            return DriverManager.getConnection(Constants.DB_URL, Constants.DB_USER, Constants.DB_PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException("DB 연결 실패: URL/계정/권한을 확인하세요.", e);
        }
    }
}

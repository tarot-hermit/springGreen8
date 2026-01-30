package app;

import util.DBUtil;
import java.sql.Connection;

public class DBTest {
    public static void main(String[] args) {
        try (Connection con = DBUtil.getConnection()) {
            System.out.println("DB 연결 성공: " + con.getMetaData().getURL());
        } catch (Exception e) {
            System.out.println("DB 연결 실패!");
            e.printStackTrace();
        }
    }
}

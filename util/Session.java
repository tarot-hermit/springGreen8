package util;

import model.UserVO;

public final class Session {
    private Session() {}

    private static UserVO loginUser;

    public static UserVO getUser() { return loginUser; }
    public static void setUser(UserVO user) { loginUser = user; }

    public static boolean isLoggedIn() { return loginUser != null; }
    public static void logout() { loginUser = null; }
}

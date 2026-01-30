package model;

/**
 * 로그인 사용자 정보(세션 보관용)
 * 실무에서는 password를 평문으로 저장/조회하지 않고 해시 처리합니다.
 */
public class UserVO {
    private int userId;
    private String loginId;
    private String name;
    private String role;

		public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getLoginId() { return loginId; }
    public void setLoginId(String loginId) { this.loginId = loginId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
 
    public String getRole() {return role;}
    public void setRole(String role) {this.role = role;}
		
    
		public boolean isAdmin() {
			return "ADMIN".equalsIgnoreCase(role);
		}
    
    
}

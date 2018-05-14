package Common;

public class AuthRequest extends Request {
	private final String username;
	private final String password;
	private boolean admin;
	
	public AuthRequest(Code c, String username, String password) {
		super(c);
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
}

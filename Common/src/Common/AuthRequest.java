package Common;

public class AuthRequest extends Request {
	private final String username;
	private final String password;
	
	public AuthRequest(Code c, String uname, String pword) {
		super(c);
		username = uname;
		password = pword;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
}

package Common;

public class UserDetailsRequest extends Request {
	private final String username;
	
	public UserDetailsRequest(Code code, String username) {
		super(code);
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
}

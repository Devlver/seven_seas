package Common;

public class IdRequest extends Request {
	private final String user;
	
	public IdRequest(Code c, String username) {
		super(c);
		user = username;
	}
	
	public String getUser() {
		return user;
	}
}

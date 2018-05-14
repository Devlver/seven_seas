package Common;

public class UserDetailsResponse extends Response {
	private final int id;
	
	public UserDetailsResponse(Code c, int id) {
		super(c);
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}

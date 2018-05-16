package Common;

public class UserDetailsResponse extends Response {
	private final int id;
	private final String username;
	private final String email;
	private final String fullName;
	private final String cabinNumber;
	
	public UserDetailsResponse(Code c, int id, String username, String email, String fullName, String cabinNumber) {
		super(c);
		this.id = id;
		this.username = username;
		this.email = email;
		this.fullName = fullName;
		this.cabinNumber = cabinNumber;
	}
	
	public int getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public String getCabinNumber() {
		return cabinNumber;
	}
}

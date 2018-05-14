package Common;

public class RegistrationRequest extends Request {
	private final String email;
	private final String username;
	private final String cabinNumber;
	private final String fullName;
	private final String password;
	
	public RegistrationRequest(Code c, String email, String username, String cabinNumber, String fullName, String password) {
		super(c);
		this.email = email;
		this.username = username;
		this.cabinNumber = cabinNumber;
		this.fullName = fullName;
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getCabinNumber() {
		return cabinNumber;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public String getPassword() {
		return password;
	}
}

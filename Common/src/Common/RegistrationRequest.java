package Common;

public class RegistrationRequest extends Request {
	private final String email;
	private final String username;
	private final String cabinNumber;
	private final String fullName;
	private final String password;
	
	public RegistrationRequest(Code c, String mail, String uname, String cNumber, String fName, String pword) {
		super(c);
		email = mail;
		username = uname;
		cabinNumber = cNumber;
		fullName = fName;
		password = pword;
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

package GUI.Network;

import Common.*;

import java.io.IOException;
import java.io.ObjectInputStream;

public final class Account {
	private static int currentUser;
	private static String fullName;
	private static String username;
	private static String email;
	private static String cabinNumber;
	
	
	public static int getCurrentUser() {
		return currentUser;
	}
	
	public static void setCurrentUser(int user) {
		currentUser = user;
	}
	
	/**
	 * Sends the supplied username and password to the server
	 *
	 * @param username Username
	 * @param password Password
	 * @return Authorization status
	 */
	public static Code Authorize(String username, String password) throws ClassNotFoundException, IOException {
		SocketInstance sock = SocketInstance.getInstance();
		
		AuthRequest packet;
		
		// First we check if the supplied username is an email or not. If it is we ask server to validate user by email
		if (ValidateEmail(username)) {
			packet = new AuthRequest(Code.AUTHENTICATE_EMAIL, username, password);
		} else {
			packet = new AuthRequest(Code.AUTHENTICATE_USERNAME, username, password);
		}
		
		sock.Send(packet.GetSerialized());
		
		Response responsePacket;
		
		ObjectInputStream os = new ObjectInputStream(sock.getSocketInputStream());
		
		responsePacket = (Response) os.readObject();
		
		os.close();
		sock.Close();
		
		return responsePacket.getCode();
	}
	
	/**
	 * Sends a registration request to the server
	 *
	 * @param email       E-mail
	 * @param username    Username
	 * @param cabinNumber Cabin number
	 * @param fullName    Full name
	 * @param password    Password
	 * @return Registration success
	 */
	public static boolean CreateAccount(String email, String username, String cabinNumber, String fullName, String password) throws Exception {
		RegistrationRequest packet = new RegistrationRequest(Code.REGISTRATION, email, username, cabinNumber, fullName, password);
		
		SocketInstance sock = SocketInstance.getInstance();
		
		sock.Send(packet.GetSerialized());
		
		//byte[] response = sock.Receive();
		Response responsePacket;
		
		//ByteArrayInputStream baos = new ByteArrayInputStream(response);
		ObjectInputStream os = new ObjectInputStream(sock.getSocketInputStream());
		
		responsePacket = (Response) os.readObject();
		
		os.close();
		sock.Close();
		
		return responsePacket.getCode() == Code.REGISTRATION_SUCCESS;
	}
	
	/**
	 * Gets a user id by username
	 *
	 * @param username Username or email
	 * @return User id
	 */
	public static UserDetailsResponse GetUserDetails(String username) throws ClassNotFoundException, IOException {
		SocketInstance sock = SocketInstance.getInstance();
		
		Request request;
		
		if (ValidateEmail(username)) {
			request = new UserDetailsRequest(Code.GET_USER_ID_BY_EMAIL, username);
		} else
			request = new UserDetailsRequest(Code.GET_USER_ID_BY_USERNAME, username);
		
		sock.Send(request.GetSerialized());
		
		UserDetailsResponse responsePacket;
		ObjectInputStream os = new ObjectInputStream(sock.getSocketInputStream());
		
		responsePacket = (UserDetailsResponse) os.readObject();
		
		os.close();
		sock.Close();
		
		return responsePacket;
	}
	
	/**
	 * Validates a string containing email address using regex. Returns true if it is correct
	 *
	 * @param email Email address string
	 * @return Result of validation
	 */
	private static boolean ValidateEmail(String email) {
		return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$");
	}
	
	public static String getFullName() {
		return fullName;
	}
	
	public static void setFullName(String fullName) {
		Account.fullName = fullName;
	}
	
	public static String getUsername() {
		return username;
	}
	
	public static void setUsername(String username) {
		Account.username = username;
	}
	
	public static String getEmail() {
		return email;
	}
	
	public static void setEmail(String email) {
		Account.email = email;
	}
	
	public static String getCabinNumber() {
		return cabinNumber;
	}
	
	public static void setCabinNumber(String cabinNumber) {
		Account.cabinNumber = cabinNumber;
	}
}

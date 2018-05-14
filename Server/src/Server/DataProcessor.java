package Server;

import Common.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;
import java.util.ArrayList;

class DataProcessor {
	/**
	 * Takes the byte array received from the client and processes it based on a server code
	 *
	 * @param input Byte array
	 * @return Serialized response byte array
	 * @throws IOException            IOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 */
	byte[] Process(byte[] input) throws IOException, ClassNotFoundException {
		// Two streams are required to deserialize the byte array received
		// ByteArrayInputStream is able to read a byte array
		// While ObjectInputStream is able to convert it to our custom defined class
		ByteArrayInputStream bais = new ByteArrayInputStream(input);
		ObjectInputStream ois = new ObjectInputStream(bais);
		
		// First we cast it to a Common.Request class which only contains the server code
		Request packet = (Request) ois.readObject();
		
		bais.close();
		ois.close();
		
		// After we have a request code, we may decide what to do next
		switch (packet.getCode()) {
			case AUTHENTICATE_EMAIL: {
				ByteArrayInputStream bais1 = new ByteArrayInputStream(input);
				ObjectInputStream ois1 = new ObjectInputStream(bais1);
				
				// Now casting input byte array to a custom Common.AuthRequest class which contains all information
				// needed for logging the user in
				AuthRequest reqPacket = (AuthRequest) ois1.readObject();
				
				bais1.close();
				ois1.close();
				
				// Based on the result, we serialize a new Common.Response object with a proper server code, indicating either
				// a fail or a success
				try {
					return new Response(Authorize(reqPacket.getUsername(), reqPacket.getPassword(), true)).GetSerialized();
				} catch (SQLException e) {
					e.printStackTrace();
					return new Response(Code.FAIL).GetSerialized();
				}
			}
			case AUTHENTICATE_USERNAME: {
				ByteArrayInputStream bais2 = new ByteArrayInputStream(input);
				ObjectInputStream ois2 = new ObjectInputStream(bais2);
				
				AuthRequest reqPacket = (AuthRequest) ois2.readObject();
				
				bais2.close();
				ois2.close();
				
				try {
					return new Response(Authorize(reqPacket.getUsername(), reqPacket.getPassword(), false)).GetSerialized();
				} catch (SQLException e) {
					e.printStackTrace();
					return new Response(Code.FAIL).GetSerialized();
				}
			}
			case REGISTRATION: {
				ByteArrayInputStream bais3 = new ByteArrayInputStream(input);
				ObjectInputStream ois3 = new ObjectInputStream(bais3);
				
				RegistrationRequest reqPacket = (RegistrationRequest) ois3.readObject();
				
				bais3.close();
				ois3.close();
				
				try {
					if (CreateUser(reqPacket.getEmail(), reqPacket.getUsername(), reqPacket.getFullName(), reqPacket.getCabinNumber(), reqPacket.getPassword())) {
						return new Response(Code.REGISTRATION_SUCCESS).GetSerialized();
					} else {
						return new Response(Code.REGISTRATION_FAIL).GetSerialized();
					}
				} catch (Exception e) {
					e.printStackTrace();
					return new Response(Code.FAIL).GetSerialized();
				}
			}
			case GET_USER_ID_BY_EMAIL: {
				ByteArrayInputStream bais4 = new ByteArrayInputStream(input);
				ObjectInputStream ois4 = new ObjectInputStream(bais4);
				
				UserDetailsRequest reqPacket = (UserDetailsRequest) ois4.readObject();
				
				bais4.close();
				ois4.close();
				
				try {
					return new UserDetailsResponse(Code.SUCCESS, GetUserId(reqPacket.getUsername(), true)).GetSerialized();
				} catch (Exception e) {
					return new UserDetailsResponse(Code.FAIL, -1).GetSerialized();
				}
			}
			case GET_USER_ID_BY_USERNAME: {
				ByteArrayInputStream bais5 = new ByteArrayInputStream(input);
				ObjectInputStream ois5 = new ObjectInputStream(bais5);
				
				UserDetailsRequest reqPacket = (UserDetailsRequest) ois5.readObject();
				
				bais5.close();
				ois5.close();
				
				try {
					return new UserDetailsResponse(Code.SUCCESS, GetUserId(reqPacket.getUsername(), false)).GetSerialized();
				} catch (Exception e) {
					return new UserDetailsResponse(Code.FAIL, -1).GetSerialized();
				}
			}
			case GET_EXCURSIONS: {
				try {
					return new ExcursionListResponse(Code.SUCCESS, GetExcursion()).GetSerialized();
				} catch (Exception e) {
					return new ExcursionListResponse(Code.FAIL, null).GetSerialized();
				}
			}
			
			case BOOK_REQUEST: {
				ByteArrayInputStream bais6 = new ByteArrayInputStream(input);
				ObjectInputStream ois6 = new ObjectInputStream(bais6);
				
				BookRequest request = (BookRequest) ois6.readObject();
				
				try {
					return new Response(Book(request.getUserId(), request.getExcursionId(),
							request.getDate(), request.getPassengerNumber())).GetSerialized();
				} catch (Exception ex) {
					ex.printStackTrace();
					return new Response(Code.FAIL).GetSerialized();
				}
			}
			
			case GET_USER_EXCURSIONS: {
				ByteArrayInputStream bais7 = new ByteArrayInputStream(input);
				ObjectInputStream ois7 = new ObjectInputStream(bais7);
				
				IndividualRequest request = (IndividualRequest) ois7.readObject();
				
				try {
					return new BookedExcursionListResponse(Code.SUCCESS, GetBookedExcursion(request.getId())).GetSerialized();
				} catch (Exception ex) {
					ex.printStackTrace();
					return new BookedExcursionListResponse(Code.FAIL, null).GetSerialized();
				}
			}
			case EDIT_REQUEST: {
				ByteArrayInputStream bais8 = new ByteArrayInputStream(input);
				ObjectInputStream ois8 = new ObjectInputStream(bais8);
				
				BookingEditRequest request = (BookingEditRequest) ois8.readObject();
				
				try {
					return new Response(EditBooking(request.getId(), request.getPassengers(), request.getDate())).GetSerialized();
				}
				catch (Exception ex) {
					ex.printStackTrace();
					return new Response(Code.FAIL).GetSerialized();
				}
			}
			case CANCEL_REQUEST: {
				ByteArrayInputStream bais8 = new ByteArrayInputStream(input);
				ObjectInputStream ois8 = new ObjectInputStream(bais8);
				
				IndividualRequest request = (IndividualRequest) ois8.readObject();
				
				try {
					return new Response(CancelBooking(request.getId())).GetSerialized();
				}
				catch (Exception ex) {
					ex.printStackTrace();
					return new Response(Code.FAIL).GetSerialized();
				}
			}
			case ADMIN_BOOKINGS_REQUEST: {
				ByteArrayInputStream bais8 = new ByteArrayInputStream(input);
				ObjectInputStream ois8 = new ObjectInputStream(bais8);
				
				IndividualRequest request = (IndividualRequest) ois8.readObject();
				
				try {
					return new AdminBookingResponse(Code.SUCCESS, GetAdminBookings(request.getId())).GetSerialized();
				} catch (SQLException e) {
					e.printStackTrace();
					return new AdminBookingResponse(Code.FAIL, null).GetSerialized();
				}
			}
			default: {
				return new Response(Code.FAIL).GetSerialized();
			}
			
		}
	}
	
	/**
	 * Checks the database records for supplied username and password
	 *
	 * @param username Username or email
	 * @param password Password
	 * @param email    True for email validation, false for username validation
	 * @return Authentication result
	 */
	private Code Authorize(String username, String password, boolean email) throws SQLException, ClassNotFoundException {
		boolean authenticationSuccess;
		
		String query;
		
		// Different queries for username and email validation
		if (!email)
			query = "SELECT hash, admin_id FROM account WHERE username = ?";
		else
			query = "SELECT hash, admin_id FROM account WHERE email = ?";
		
		
		// Prepared statements protect against SQL injections
		PreparedStatement statement = getPreparedStatement(query);
		
		statement.setString(1, username);
		
		// Result set contains everything that database returned for current query
		// By default, the cursor is set outside of the table, so we have to call next() to move cursor to first column
		ResultSet result = statement.executeQuery();
		result.next();
		
		// Getting the resulting hash for supplied username or email from result set
		String resHash = result.getString("hash");
		
		authenticationSuccess = BCrypt.checkpw(password, resHash);
		
		Object admin = result.getObject("admin_id");
		
		if (authenticationSuccess) {
			if (admin != null)
				return Code.AUTHENTICATE_SUCCESS_ADMIN;
			else
				return Code.AUTHENTICATE_SUCCESS;
		} else
			return Code.AUTHENTICATE_FAIL;
	}
	
	/**
	 * Creates a new user in the database
	 *
	 * @param email       E-mail
	 * @param username    Username
	 * @param fullName    Full name
	 * @param cabinNumber Cabin number
	 * @param password    Password
	 * @return Creation success
	 */
	private boolean CreateUser(String email, String username, String fullName, String cabinNumber, String password) throws Exception {
		String query = "INSERT INTO account(username, full_name, cabin_number, hash, email, admin_id)" +
				" VALUES (?, ?, ?, ?, ?, ?)";
		
		// Prepared statements protect against SQL injections
		PreparedStatement statement = getPreparedStatement(query);
		
		// Values are placed at '?' in query. Unlike usually in programming, index starts at 1
		statement.setString(1, username);
		statement.setString(2, fullName);
		statement.setString(3, cabinNumber);
		statement.setString(4, BCrypt.hashpw(password, BCrypt.gensalt()));
		statement.setString(5, email);
		
		// Not an admin account. I suppose admin accounts would be created by hand via dedicated tool
		// if it was a real world application. But for now we assume excursion administrator cannot register via
		// client
		statement.setNull(6, Types.INTEGER);
		
		statement.execute();
		
		// Return true if more than 0 rows affected
		return statement.getUpdateCount() > 0;
	}
	
	/**
	 * Retrieves a user ID number from the database
	 *
	 * @param username Username
	 * @param email    Check against username or email
	 * @return ID
	 */
	private int GetUserId(String username, boolean email) throws Exception {
		String query;
		
		if (email) {
			query = "SELECT id FROM account WHERE email = ?";
		} else
			query = "SELECT id FROM account WHERE username = ?";
		
		PreparedStatement statement = getPreparedStatement(query);
		
		statement.setString(1, username);
		
		ResultSet result = statement.executeQuery();
		result.next();
		
		return result.getInt("id");
	}
	
	/**
	 * Retrieves all excursions from the database
	 *
	 * @return ArrayList that contains all excursion wrapped into Common.Excursion objects
	 */
	private ArrayList<Excursion> GetExcursion() throws Exception {
		String query = "SELECT * FROM excursion";
		
		PreparedStatement statement = getPreparedStatement(query);
		
		ResultSet result = statement.executeQuery();
		
		ArrayList<Excursion> excursions = new ArrayList<>();
		
		while (result.next()) {
			excursions.add(new Excursion(result.getInt("ID"), result.getString("PORT_ID"), result.getString("NAME"), ""));
		}
		
		return excursions;
	}
	
	private ArrayList<BookedExcursion> GetBookedExcursion(int userId) throws Exception {
		String query = "SELECT booking.id, NAME, PORT_ID, date, passenger_number, wait_list FROM booking" +
				" INNER JOIN excursion ON excursion_id = excursion.ID WHERE user_id = ?";
		
		PreparedStatement statement = getPreparedStatement(query);
		statement.setInt(1, userId);
		
		ResultSet result = statement.executeQuery();
		
		ArrayList<BookedExcursion> excursions = new ArrayList<>();
		
		while(result.next())
		{
			if(!result.getBoolean("wait_list")) {
				
				excursions.add(new BookedExcursion(result.getInt("id"),
						result.getString("NAME"),
						result.getString("PORT_ID"),
						"Confirmed",
						result.getInt("passenger_number"),
						result.getDate("date")
				));
			}
			else {
				excursions.add(new BookedExcursion(
						result.getInt("id"),
						result.getString("NAME"),
						result.getString("PORT_ID"),
						"Waiting list",
						result.getInt("passenger_number"),
						result.getDate("date")
				));
			}
		}
		
		return excursions;
	}
	
	private Code Book(int id, int excursionId, Date date, int passengerCount) throws SQLException, ClassNotFoundException {
		int numberOfPassengers = CheckExcursionAvailability(excursionId, date);
		
		Code returnCode = Code.FAIL;
		
		String query = "INSERT INTO booking(user_id, excursion_id, date, passenger_number, wait_list) VALUES(?, ?, ?, ?, ?)";
		
		PreparedStatement statement = getPreparedStatement(query);
		statement.setInt(1, id);
		statement.setInt(2, excursionId);
		statement.setDate(3, date);
		statement.setInt(4, passengerCount);
		if(CheckAlreadyBooked(id, excursionId, date))
			return Code.BOOKED_ALREADY;
		else if(numberOfPassengers >= 32) {
			statement.setBoolean(5, true);
			returnCode = Code.BOOKED_WAIT_LIST;
		}
		else if (numberOfPassengers + passengerCount > 32)
			return Code.BOOK_REMOVE_PASSENGERS;
		else if(numberOfPassengers + passengerCount < 32) {
			statement.setBoolean(5, false);
			returnCode = Code.BOOKED;
		}
		
		statement.execute();
		
		return returnCode;
	}
	
	private Code CancelBooking(int id) throws SQLException, ClassNotFoundException {
		String query = "DELETE FROM booking WHERE id = ?";
		
		PreparedStatement statement = getPreparedStatement(query);
		statement.setInt(1, id);
		
		statement.execute();
		
		if(statement.getUpdateCount() > 0) {
			return Code.SUCCESS;
		}
		else {
			return Code.FAIL;
		}
	}
	
	private Code EditBooking(int id, int passengers, Date date) throws SQLException, ClassNotFoundException {
		String query = "SELECT excursion_id FROM booking WHERE id = ?";
		
		PreparedStatement statement = getPreparedStatement(query);
		statement.setInt(1, id);
		
		ResultSet result = statement.executeQuery();
		result.next();
		
		int exId = result.getInt(1);
		
		if(CheckExcursionAvailability(exId, date) >= 32)
			return Code.BOOK_FULL;
		
		query = "UPDATE booking SET passenger_number = ?, date = ? WHERE id = ?";
		
		PreparedStatement statement1 = getPreparedStatement(query);
		statement1.setInt(1, passengers);
		statement1.setDate(2, date);
		statement1.setInt(3, id);
		
		statement1.execute();
		
		if(statement1.getUpdateCount() != 0)
			return Code.EDIT_SUCCESS;
		else
			return Code.FAIL;
	}
	
	private boolean CheckAlreadyBooked(int userId, int excursionId, Date date) throws SQLException, ClassNotFoundException {
		String query = "SELECT excursion_id FROM booking WHERE  user_id = ? and date = ?";
		
		PreparedStatement statement = getPreparedStatement(query);
		statement.setInt(1, userId);
		statement.setDate(2, date);
		
		ResultSet result = statement.executeQuery();
		
		while(result.next()) {
			if(result.getInt("excursion_id") == excursionId)
				return true;
		}
		
		return false;
	}
	
	private int CheckExcursionAvailability(int excursionId, Date date) throws SQLException, ClassNotFoundException {
		String query = "SELECT passenger_number FROM booking WHERE excursion_id = ? AND date = ?";
		
		PreparedStatement statement = getPreparedStatement(query);
		statement.setInt(1, excursionId);
		statement.setDate(2, date);
		ResultSet result = statement.executeQuery();
		
		ArrayList<Integer> rows = new ArrayList<>();
		
		while (result.next()) {
			rows.add(result.getInt("passenger_number"));
		}
		
		int resultPassenger = 0;
		
		for (int count : rows) {
			resultPassenger += count;
		}
		
		return resultPassenger;
	}
	
	private ArrayList<AdminBooking> GetAdminBookings(int userId) throws SQLException, ClassNotFoundException {
		String query = "SELECT admin_id FROM account WHERE id = ?";
		
		PreparedStatement statement = getPreparedStatement(query);
		statement.setInt(1, userId);
		
		ResultSet result = statement.executeQuery();
		result.next();
		
		int admin_id = result.getInt("admin_id");
		
		if (admin_id == 0)
			return null;
		
		return null;
	}
	
	
	private PreparedStatement getPreparedStatement(String query) throws ClassNotFoundException, SQLException {
		
		Class.forName("com.mysql.cj.jdbc.Driver"); // Making sure there is a correct driver for mysql
		
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sevenseas?autoReconnect=true&" +
				"useSSL=false", "sevenseas", "8fDwFqklhLAmO5DT");
		
		return connection.prepareStatement(query);
	}
}

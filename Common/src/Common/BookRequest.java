package Common;

import java.sql.Date;

public class BookRequest extends Request {
	private final int userId;
	private final int excursionId;
	private final int passengerCount;
	private final Date date;
	
	public BookRequest(Code c, int user, int excId, Date dt, int passengers) {
		super(c);
		date = dt;
		userId = user;
		excursionId = excId;
		passengerCount = passengers;
	}
	
	public Date getDate() {
		return date;
	}
	
	public int getExcursionId() {
		return excursionId;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public int getPassengerCount() {
		return passengerCount;
	}
}

package Common;

import java.sql.Date;

public class BookRequest extends Request {
	private final int userId;
	private final int excursionId;
	private final int passengerNumber;
	private final Date date;
	
	public BookRequest(Code c, int userId, int excursionId, Date date, int passengerNumber) {
		super(c);
		this.date = date;
		this.userId = userId;
		this.excursionId = excursionId;
		this.passengerNumber = passengerNumber;
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
	
	public int getPassengerNumber() {
		return passengerNumber;
	}
}

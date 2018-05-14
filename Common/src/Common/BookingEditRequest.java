package Common;

import java.sql.Date;

public class BookingEditRequest extends Request {
	private final int id;
	private final int passengers;
	private final Date date;
	
	public BookingEditRequest(Code c, int id, int passengers, Date date) {
		super(c);
		this.id = id;
		this.passengers = passengers;
		this.date = date;
	}
	
	public int getId() {
		return id;
	}
	
	public int getPassengers() {
		return passengers;
	}
	
	public Date getDate() {
		return date;
	}
}

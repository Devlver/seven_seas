package Common;

import java.sql.Date;

public class BookingEditRequest extends Request {
	private int Id;
	private int Passengers;
	private Date Date;
	
	public BookingEditRequest(Code c, int id, int passengers, Date date) {
		super(c);
		Id = id;
		Passengers = passengers;
		Date = date;
	}
	
	public int getId() {
		return Id;
	}
	
	public int getPassengers() {
		return Passengers;
	}
	
	public Date getDate() {
		return Date;
	}
}

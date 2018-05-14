package Common;

import java.util.ArrayList;

public class AdminBookingResponse extends Response {
	private final ArrayList<AdminBooking> bookings;
	
	public AdminBookingResponse(Code c, ArrayList<AdminBooking> bookings) {
		super(c);
		this.bookings = bookings;
	}
	
	public ArrayList<AdminBooking> getBookings() {
		return bookings;
	}
}

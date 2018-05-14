package Common;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import java.util.Date;

public class AdminBooking extends RecursiveTreeObject<AdminBooking> {
	private final int bookingId;
	private final String passengerName;
	private final Date bookingDate;
	private final int passengersNumber;
	private final String status;
	
	public AdminBooking(int bookingId, String passengerName, Date bookingDate, int passengersNumber, String status) {
		this.bookingId = bookingId;
		this.passengerName = passengerName;
		this.bookingDate = bookingDate;
		this.passengersNumber = passengersNumber;
		this.status = status;
	}
	
	public String getPassengerName() {
		return passengerName;
	}
	
	public Date getBookingDate() {
		return bookingDate;
	}
	
	public String getPassengersNumber() {
		return String.valueOf(passengersNumber);
	}
	
	public String getStatus() {
		return status;
	}
	
	public int getBookingId() {
		return bookingId;
	}
}

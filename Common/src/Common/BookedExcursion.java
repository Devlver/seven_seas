package Common;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import java.io.Serializable;
import java.sql.Date;

public class BookedExcursion extends RecursiveTreeObject<BookedExcursion> implements Serializable {
	private final int id;
	private final String name;
	private final String port;
	private final int passengerNumber;
	private final String status;
	private final Date date;
	
	public BookedExcursion(int id, String name, String port, String status, int passengerNumber, Date date) {
		this.id = id;
		this.name = name;
		this.port = port;
		this.status = status;
		this.date = date;
		this.passengerNumber = passengerNumber;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPort() {
		return port;
	}
	
	public String getStatus() {
		return status;
	}
	
	public Date getDate() {
		return date;
	}
	
	public int getId() {
		return id;
	}
	
	public String getPassengerNumber() {
		return String.valueOf(passengerNumber);
	}
}

package Common;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import java.io.Serializable;
import java.sql.Date;

public class BookedExcursion extends RecursiveTreeObject<BookedExcursion> implements Serializable {
	private int Id;
	private String Name;
	private String Port;
	private int Passengers;
	private String Status;
	private Date Date;
	
	public BookedExcursion(int id, String name, String port, String status, int passengers, Date date) {
		Id = id;
		Name = name;
		Port = port;
		Status = status;
		Date = date;
		Passengers = passengers;
	}
	
	public String getName() {
		return Name;
	}
	
	public String getPort() {
		return Port;
	}
	
	public String getStatus() {
		return Status;
	}
	
	public Date getDate() {
		return Date;
	}
	
	public int getId() {
		return Id;
	}
	
	public String getPassengers() {
		return String.valueOf(Passengers);
	}
}

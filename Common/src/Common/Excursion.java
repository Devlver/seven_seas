package Common;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import java.io.Serializable;

public class Excursion extends RecursiveTreeObject<Excursion> implements Serializable {
	private final String PortId;
	private final String Name;
	private final int Id;
	private final String Status;
	
	public Excursion(int id, String portId, String name, String status) {
		Id = id;
		PortId = portId;
		Name = name;
		Status = status;
	}
	
	public int getId() {
		return Id;
	}
	
	public String getPortId() {
		return PortId;
	}
	
	public String getName() {
		return Name;
	}
	
	public String getStatus() {
		return Status;
	}
}

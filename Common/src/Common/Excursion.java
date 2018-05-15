package Common;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import java.io.Serializable;

public class Excursion extends RecursiveTreeObject<Excursion> implements Serializable {
	private final String portId;
	private final String name;
	private final int id;
	
	public Excursion(int id, String portId, String name) {
		this.id = id;
		this.portId = portId;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getPortId() {
		return portId;
	}
	
	public String getName() {
		return name;
	}
}

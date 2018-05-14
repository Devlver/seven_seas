package Common;

import java.io.Serializable;
import java.util.ArrayList;

public class ExcursionListResponse extends Response implements Serializable {
	private final ArrayList<Excursion> excursions;
	
	public ExcursionListResponse(Code c, ArrayList<Excursion> excursions) {
		super(c);
		this.excursions = excursions;
	}
	
	public ArrayList<Excursion> getExcursions() {
		return excursions;
	}
}

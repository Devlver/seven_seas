package Common;

import java.io.Serializable;
import java.util.ArrayList;

public class ExcursionListResponse extends Response implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final ArrayList<Excursion> excursions;
	
	public ExcursionListResponse(Code c, ArrayList<Excursion> arrayList) {
		super(c);
		excursions = arrayList;
	}
	
	public ArrayList<Excursion> getExcursions() {
		return excursions;
	}
}

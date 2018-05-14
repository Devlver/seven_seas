package Common;

import java.util.ArrayList;

public class BookedExcursionListResponse extends Response {
	private final ArrayList<BookedExcursion> excursions;
	
	public BookedExcursionListResponse(Code c, ArrayList<BookedExcursion> excursions) {
		super(c);
		this.excursions = excursions;
	}
	
	public ArrayList<BookedExcursion> getExcursions() {
		return excursions;
	}
}

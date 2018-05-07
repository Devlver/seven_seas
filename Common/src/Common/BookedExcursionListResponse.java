package Common;

import java.util.ArrayList;

public class BookedExcursionListResponse extends Response {
	private ArrayList<BookedExcursion> excursions;
	
	public BookedExcursionListResponse(Code c, ArrayList<BookedExcursion> a) {
		super(c);
		excursions = a;
	}
	
	public ArrayList<BookedExcursion> getExcursions() {
		return excursions;
	}
}

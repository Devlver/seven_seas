package Common;

public class IndividualRequest extends Request {
	private final int id;
	
	public IndividualRequest(Code c, int id) {
		super(c);
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}

package Common;

public class IndividualRequest extends Request {
	private int Id;
	
	public IndividualRequest(Code c, int id) {
		super(c);
		Id = id;
	}
	
	public int getId() {
		return Id;
	}
}

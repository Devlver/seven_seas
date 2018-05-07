package Common;

public class IdResponse extends Response {
	private final int id;
	
	public IdResponse(Code c, int idN) {
		super(c);
		id = idN;
	}
	
	public int getId() {
		return id;
	}
}

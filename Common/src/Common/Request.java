package Common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Describes a serializable request sent to the server,
 * all request classes must inherit
 */
public class Request implements Serializable {
	private final Code code;
	
	public Request(Code c) {
		code = c;
	}
	
	/**
	 * Serialize this class
	 *
	 * @return Serialized byte array of this class
	 */
	public byte[] GetSerialized() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(baos);
		os.writeObject(this);
		os.close();
		return baos.toByteArray();
	}
	
	public Code getCode() {
		return code;
	}
}

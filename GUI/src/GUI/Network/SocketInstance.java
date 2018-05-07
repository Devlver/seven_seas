package GUI.Network;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.OutputStream;

/**
 * Client socket for sending and receiving messages
 */
public class SocketInstance {
	private java.net.Socket sock;
	
	private SocketInstance() {
		try {
			sock = new java.net.Socket("178.62.30.181", 5555);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * [GET] A new instance of client socket
	 *
	 * @return SocketInstance client
	 */
	public static SocketInstance getInstance() {
		return new SocketInstance();
	}
	
	/**
	 * Sends a byte array to the server
	 *
	 * @param bytes Byte array to send
	 */
	public void Send(byte[] bytes) throws IOException {
		OutputStream out = sock.getOutputStream();
		out.write(bytes);
	}
	
	/**
	 * Receives a byte array from server
	 *
	 * @return Byte array received
	 */
	public byte[] Receive() throws IOException {
		InputStream is;
		
		is = sock.getInputStream();
		
		// yeah, this is a magical number. A size of excursions list which is not going to change anyway
		byte[] input = new byte[83399];
		if (is.read(input) != 0) {
			return input;
		} else {
			throw new InvalidObjectException("Server returned empty result");
		}
	}
	
	public InputStream getSocketInputStream() throws IOException {
		return sock.getInputStream();
	}
	
	public void Close() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

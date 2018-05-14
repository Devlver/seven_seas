package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class Client extends Thread {
	private final DataProcessor processor = new DataProcessor();
	private final Socket cl;
	private volatile boolean running = true;
	
	Client(Socket client) {
		cl = client;
	}
	
	@Override
	public void run() {
		while (running) {
			try {
				InputStream in = cl.getInputStream();
				OutputStream out = cl.getOutputStream();
				
				byte[] input = new byte[8192];
				
				//noinspection ResultOfMethodCallIgnored
				in.read(input);
				
				try {
					out.write(processor.Process(input));
					out.flush();
					cl.close();
					running = false;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		running = false;
	}
}

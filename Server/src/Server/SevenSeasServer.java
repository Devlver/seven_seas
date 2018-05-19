package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class SevenSeasServer {
	private ServerSocket sock;
	
	SevenSeasServer() throws IOException {
		sock = new ServerSocket(5555);
		System.out.println("Server started at " + sock.getLocalSocketAddress());
		System.out.println("Waiting for clients...");
	}
	
	/**
	 * Starts the server
	 */
	void Run() {
		boolean running = true;
		
		while (running) {
			try {
				Socket client = sock.accept();
				System.out.println("Received connection from" + client.getRemoteSocketAddress());
				
				Client proc = new Client(client);
				proc.start();
			}
			// Even if an exception occurs, we don't want the server to shut down 
			catch (Exception ex) {
				//ex.printStackTrace();
				running = false;
			}
		}
	}
}

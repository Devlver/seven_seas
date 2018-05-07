package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class SevenSeasServer {
	private ServerSocket sock;
	
	SevenSeasServer(int port) throws IOException {
		sock = new ServerSocket(port);
		System.out.println("Server started at " + sock.getLocalSocketAddress());
		System.out.println("Waiting for clients...");
	}
	
	/**
	 * Starts the server
	 */
	public void Run() {
		while (true) {
			try {
				Socket client = sock.accept();
				System.out.println("Received connection from" + client.getRemoteSocketAddress());
				
				Client proc = new Client(client);
				proc.start();
			}
			// Even if an exception occurs, we don't want the server to shut down 
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}

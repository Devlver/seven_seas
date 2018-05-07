package Server;

import java.io.IOException;

class Main {
	public static void main(final String[] args) {
		try {
			SevenSeasServer server = new SevenSeasServer(5555);
			server.Run();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}

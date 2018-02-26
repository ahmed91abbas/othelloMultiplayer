//package othelloMultiplayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) {
		Monitor monitor = new Monitor();
		int nbrOfClients = 0;
		ServerSocket server = null;
		Socket client = null;
		int portNbr = args.length == 0 ? 30000 : Integer.parseInt(args[0]);
		try {
			server = new ServerSocket(portNbr);
			System.out.println("Server is running at port " + portNbr);
		} catch (IOException e) {
			System.out.println("Error creating serversocket");
		}

		while (!Thread.interrupted() && nbrOfClients < 2) {
			try {
				client = server.accept();
				String color = nbrOfClients == 0 ? "Black" : "White";
				nbrOfClients++;
				new ThreadClient(monitor, client, color).start();
			} catch (IOException e) {
				System.out.println("Closing the server");
			}
		}
	}
}

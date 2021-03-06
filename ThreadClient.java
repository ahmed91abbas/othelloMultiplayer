import java.io.*;
import java.net.*;

public class ThreadClient extends Thread {

	private Socket client;
	private Monitor monitor;
	private String color;
	private int nbrConnected;

	public ThreadClient(Monitor monitor, Socket client, String color, int nbrConnected) {
		this.client = client;
		this.monitor = monitor;
		this.color = color;
		this.nbrConnected = nbrConnected;
	}

	@Override
	public String toString() {
		return color;
	}

	@Override
	public boolean equals(Object e) {
		return e.toString().equals(this.toString());
	}

	@Override
	public void run() {
		try {
			Writer out = new OutputStreamWriter(client.getOutputStream()); // out for server
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
			String msg = color + ";" + nbrConnected + ";";
			out.write(msg);
			out.flush();
			msg = "Connected to " + client.getInetAddress() + "\nYou got the color " + color + "\n";
			out.write(msg);
			out.flush();
			monitor.addClient(this, out);
			
			String input = "";
			
			while(!this.isInterrupted()) {
				input = in.readLine();
				monitor.addMsg(input, this);
			}
			
			out.write("Closing connection, bye");
			out.flush();
			client.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("I/O error" );
		}

	}
}

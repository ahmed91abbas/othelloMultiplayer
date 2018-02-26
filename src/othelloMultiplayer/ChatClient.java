
//package othelloMultiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
	private PrintWriter out;
	private String color;
	private int nbrConnected;

	public ChatClient(String machine, int portNbr) throws UnknownHostException, IOException{
		color = null;
		nbrConnected = 0;
		out = null;
		BufferedReader stdIn = null;
		BufferedReader in = null;
		Socket client = new Socket(machine, portNbr);
		out = new PrintWriter(client.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		new readerThread(in).start();
		new writerThread(out, client).start();
	}

	public void sendMsg(String msg) {
		out.println(msg);
		out.flush();
	}

	public static class readerThread extends Thread {
		private BufferedReader in;

		public readerThread(BufferedReader in) {
			this.in = in;
		}

		@Override
		public void run() {
			try {
				String out;
				while (!isInterrupted() && (out = in.readLine()) != null) {
					System.out.println("    " + out);
				}
				System.exit(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class writerThread extends Thread {
		private PrintWriter out;
		private BufferedReader stdIn;
		private Socket client;

		public writerThread(PrintWriter out, Socket client) {
			this.out = out;
			this.client = client;
			stdIn = new BufferedReader(new InputStreamReader(System.in));
		}

		@Override
		public void run() {
			try {
				String userInput;
				while ((userInput = stdIn.readLine()) != null && !client.isClosed()) {
					out.println(userInput);
					out.flush();
				}
				System.exit(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int getNbrConnected() {
		return nbrConnected;
	}

	public String getColor() {
		return color;
	}
}
//TODO update nbrconnected and color

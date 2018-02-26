
//package othelloMultiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {

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
					System.out.println(out);
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

	public static void main(String[] args) {
		PrintWriter out = null;
		BufferedReader stdIn = null;
		BufferedReader in = null;
		// if(args.length != 2) {
		// System.out.println("You need to specify machine and port nbr");
		// System.exit(1);
		// }
		// String machine = args[0];
		// int portNbr = Integer.parseInt(args[1]);
		String machine = "localhost";
		int portNbr = 30000;
		try {
			Socket client = new Socket(machine, portNbr);
			out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//			stdIn = new BufferedReader(new InputStreamReader(System.in));
			new readerThread(in).start();
			new writerThread(out, client).start();
//			String userInput;
//			while ((userInput = stdIn.readLine()) != null && !client.isClosed()) {
//				out.println(userInput);
//				out.flush();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

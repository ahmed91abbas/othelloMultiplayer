
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
	private boolean firstMsg;
	private Othello board;

	public ChatClient(String machine, int portNbr) throws UnknownHostException, IOException {
		board = null;
		firstMsg = true;
		color = null;
		nbrConnected = 0;
		out = null;
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

	public class readerThread extends Thread {
		private BufferedReader in;

		public readerThread(BufferedReader in) {
			this.in = in;
		}

		@Override
		public void run() {
			try {
				String out;
				while (!isInterrupted() && (out = in.readLine()) != null) {
					if (firstMsg) {
						String[] msgs = out.split(";");
						color = msgs[0];
						nbrConnected = Integer.parseInt(msgs[1]);
						for (int i = 2; i < msgs.length; i++) {
							System.out.println(msgs[i]);
						}
						firstMsg = false;
					} else {
						String flag = out.substring(0, 2);
						if (flag.equalsIgnoreCase(("M:"))) {
							String move = out.substring(2);
							System.out.println("Opponent made move: " + move);
							board.makeMove(move);
						} else if (flag.equalsIgnoreCase(("T:"))) {
							System.out.println("Opponent says: " + out.substring(2));
						} else if (flag.equalsIgnoreCase(("S:"))) {
							nbrConnected = Integer.parseInt(out.substring(2));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public class writerThread extends Thread {
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
					out.println("T:" + userInput);
					out.flush();
				}
				System.exit(1);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public int getNbrConnected() {
		return nbrConnected;
	}

	public String getColor() {
		return color;
	}

	public void setBoard(Othello othello) {
		board = othello;
	}
}

//package othelloMultiplayer;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

public class Monitor {
	private HashMap<ThreadClient, Writer> clients;

	public Monitor() {
		clients = new HashMap<ThreadClient, Writer>();
	}

	public synchronized void addMsg(String msg, ThreadClient client) {
		String errorMsg = "Not a supported query!\n";
		if (msg.length() < 2) {
			echoMsg(errorMsg, client);
		} else {
			String flag = msg.substring(0, 2);
			if (flag.equalsIgnoreCase(("M:"))) {
				sendMsgToAll(msg.substring(2));
			} else if (flag.equalsIgnoreCase(("E:"))) {
				echoMsg(msg.substring(2), client);
			} else if (flag.equalsIgnoreCase(("Q:"))) {
				closeConnection(client);
			} else {
				echoMsg(errorMsg, client);
			}
		}
	}

	private void closeConnection(ThreadClient client) {
		client.interrupt();
		clients.remove(client);
		System.out.println("Connected clients: " + clients.size());
	}

	private void echoMsg(String msg, ThreadClient client) {
		try {
			Writer out = clients.get(client);
			out.write(msg + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendMsgToAll(String msg) {
		try {
			for (ThreadClient client : clients.keySet()) {
				Writer out = clients.get(client);
				out.write(msg + "\n");
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized void addClient(ThreadClient threadClient, Writer out) {
		clients.put(threadClient, out);
		System.out.println("Connected clients: " + clients.size());
	}
	
	public void interruptAll() {
		for (ThreadClient client : clients.keySet()) {
			client.interrupt();
		}
	}
}

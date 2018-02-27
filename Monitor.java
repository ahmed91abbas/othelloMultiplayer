import java.io.Writer;
import java.util.HashMap;

public class Monitor {
	private HashMap<ThreadClient, Writer> clients;

	public Monitor() {
		clients = new HashMap<ThreadClient, Writer>();
	}

	public synchronized void addMsg(String msg, ThreadClient client) {
		sendMsg(msg, client);
	}

	private void sendMsg(String msg, ThreadClient sender) {
		try {
			for (ThreadClient client : clients.keySet()) {
				if (!sender.equals(client)) {
					Writer out = clients.get(client);
					out.write(msg + "\n");
					out.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendSystemMsg(String msg) {
		try {
			for (ThreadClient client : clients.keySet()) {
				Writer out = clients.get(client);
				out.write("S:" + msg + "\n");
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void addClient(ThreadClient threadClient, Writer out) {
		clients.put(threadClient, out);
		sendSystemMsg(clients.size() + "");
		System.out.println("Connected clients: " + clients.size());
	}

	public void interruptAll() {
		for (ThreadClient client : clients.keySet()) {
			client.interrupt();
		}
	}
}
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread {
	private Socket socket;
	private String userEmail;

	public ClientThread(Socket socket, String userEmail) {
		this.socket = socket;
		this.userEmail = userEmail;
	}

	@Override
	public void run() {
		/* Implement Read/Write Lock for Message Queue here */
		ArrayList<QueueObject> messageQueue = Utils.getQueueForUser(userEmail);
		if (messageQueue == null || messageQueue.isEmpty()) {
			handleExit();
			return;
		}

		Location currentLocation = Utils.getCurrentLocationForUser(userEmail);
		for (int i = 0; i < messageQueue.size();) {
			QueueObject obj = messageQueue.get(i);
			if (obj.getLocation().equals(currentLocation)) {
				messageQueue.remove(i);
				/* Logic to deliver Message to client */
			} else
				i++;
		}
	}

	public void handleExit() {
		/* Send reply, create probe thread and die */
	}
}

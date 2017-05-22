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
		if (Utils.messageQueueForUserExists(userEmail)) {
			handleExit();
			return;
		}
		Utils.deliverAllPossibleMessages(userEmail);
	}

	public void handleExit() {
		/* Send reply, create probe thread and die */
	}
}
